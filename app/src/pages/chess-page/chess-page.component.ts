import { TitleCasePipe } from "@angular/common";
import { afterNextRender, AfterViewInit, Component, effect, ElementRef, inject, Signal, signal, TemplateRef, viewChild, viewChildren } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { NgbModal } from "@ng-bootstrap/ng-bootstrap";
import { forkJoin } from 'rxjs';
import { UserApi } from "../../api/UserApi";
import { getPieceImg, Square } from "../../chess/chess-view";
import { Chessboard } from "../../chess/Chessboard";
import { CastlingMove } from "../../chess/moves/CastlingMove";
import { EnPassantMove } from "../../chess/moves/EnPassantMove";
import { Move } from "../../chess/moves/Move";
import { PromotionMove } from "../../chess/moves/PromotionMove";
import { Color } from "../../chess/pieces/AbstractPiece";
import { Piece, PieceType } from "../../chess/pieces/Piece";
import { reconstructMove, reconstructPiece } from "../../chess/utils";
import { AvatarComponent } from "../../components/avatar/avatar.component";
import { SnakeCaseToTitleCasePipe } from "../../pipes/SnakeCaseToTitleCase";
import { TimeFormatPipe } from "../../pipes/TimeFormatPipe";
import { ChessService, GameState, MatchSnapshot, MoveResult, PlayerData, ScoreboardPlayerData } from "../../services/ChessService";
import { CountDownService } from "../../services/CountDownService";

interface DraggedPiece {
    i: number,
    j: number,
    x: number,
    y: number
}

interface Player {
    id: string;
    displayName: string;
    avatar: string;
    color: Color;
    mmrBefore?: number,
    mmrAfter?: number,
    score?: number
}

@Component({
    selector: 'chess-page',
    templateUrl: 'chess-page.component.html',
    styleUrl: 'chess-page.component.css',
    standalone: true,
    imports: [TimeFormatPipe, TitleCasePipe, AvatarComponent, SnakeCaseToTitleCasePipe],
    providers: [CountDownService]
})
export class ChessPage implements AfterViewInit {

    private userApi: UserApi = inject(UserApi);

    private chessService: ChessService = inject(ChessService);

    private countDownService: CountDownService = inject(CountDownService);

    private route: ActivatedRoute = inject(ActivatedRoute);

    private modalService: NgbModal = inject(NgbModal);

    private boardElementRef = viewChild("board", { read: ElementRef });

    private pieceImgElementRefs = viewChildren("pieceImg", { read: ElementRef });

    private moveListElementRef = viewChild("moveListRef", { read: ElementRef });

    private scoreboardTemplateRef = viewChild("scoreboard", { read: TemplateRef });

    private draggedPiece?: DraggedPiece;

    readonly BOARD_SIZE: number = Chessboard.SIZE;

    readonly SQUARE_WIDTH: number = Square.WIDTH;

    readonly SQUARE_HEIGHT: number = Square.HEIGHT;

    private chessboard?: Chessboard;

    promotablePieces: PieceType[] = ["QUEEN", "ROOK", "BISHOP", "KNIGHT"];

    players = signal<Player[]>([]);

    squares: Square[][];

    moveList = signal<string[][]>([]);

    myColor = signal<Color | undefined>(undefined);

    winnerColor = signal<Color | undefined>(undefined);

    activeColor = signal<Color | undefined>(undefined);

    gameState = signal<GameState | undefined>(undefined);

    countDown: Signal<number>;

    constructor() {

        effect(() => {
            if (this.moveList().length == 0) return;
            
            const nativeElement = this.moveListElementRef()?.nativeElement;
            nativeElement.scrollTop = nativeElement.scrollHeight - nativeElement.clientHeight;
        });

        const squares = [];
        for (let i = 0; i < Chessboard.SIZE; i++) {
            const row = [];
            for (let j = 0; j < Chessboard.SIZE; j++) {
                row.push(new Square());
            }
            squares.push(row);
        }

        this.squares = squares;

        this.countDown = this.countDownService.countDown;

        this.chessService.subscribe("AUTHENTICATED", this.joinMatch);
        this.chessService.subscribe("MATCH_SNAPSHOT", this.initialize);
        this.chessService.subscribe("MOVE_RESULT", this.move);
        this.chessService.connect(this.route.snapshot.params["target"]);
    }

    ngAfterViewInit(): void {
        for (let i = 0; i < this.pieceImgElementRefs().length; i++) {
            this.squares[Math.floor(i / Chessboard.SIZE)][i % Chessboard.SIZE].setPieceImg(this.pieceImgElementRefs()[i]);
        }
    }

    private joinMatch = (): void => {
        const token = localStorage.getItem("matchmakingToken");
        localStorage.removeItem("matchmakingToken");

        this.chessService.joinMatch(token);
    }

    private initialize = (snapshot: MatchSnapshot): void => {
        const { activeColor, moves, board, players, nextTurn, state } = snapshot;

        const moveList = moves.map(m => reconstructMove(m, state));
        const pieces = board.map(p => p == null ? null : reconstructPiece(p));

        this.chessboard = new Chessboard(moveList, pieces);

        this.gameState.set(state);
        this.activeColor.set(activeColor);
        this.countDownService.start(nextTurn);
        this.myColor.set(players.find(p => p.id === this.chessService.getCurrentUserId())!.color);

        this.initBoardView(pieces);
        this.initMoveList(moveList);
        this.initPlayers(players);
    }

    private move = (payload: MoveResult): void => {
        const { activeColor, nextTurn, move, state, scoreboard, winnerColor } = payload;

        this.activeColor.set(activeColor);
        this.countDownService.start(nextTurn);
        this.gameState.set(state);

        if (move == null) {
            if (this.myTurn()) {
                this.resetDraggedPiece();
            }
        }
        else {
            const m = reconstructMove(move, state);
            m.execute(this.chessboard!);
            this.chessboard?.add(m);
            this.updateBoardView(m, !this.myMove(m));
            this.updateMoveList(m);
        }

        if (this.gameOver()) {
            this.winnerColor.set(winnerColor);
            this.initScoreboard(scoreboard!);
            this.modalService.open(this.scoreboardTemplateRef());
        }

    }

    private initScoreboard(scoreboard: ScoreboardPlayerData[]): void {
        const players = [];
        for (const player of this.players()) {
            for (const p of scoreboard) {
                if (player.id === p.id) {
                    player.score = p.score;
                    player.mmrBefore = p.mmrBefore;
                    player.mmrAfter = p.mmrAfter;
                    players.push(player);
                    break;
                }
            }
        }

        this.players.set(players);
    }

    private myMove(move: Move): boolean {
        return move.getColor() === this.myColor();
    }

    myTurn(): boolean {
        return this.activeColor() === this.myColor();
    }

    private resetDraggedPiece(): void {
        const { i, j } = this.draggedPiece!;
        this.squares[i][j].resetPiecePosition();
        this.draggedPiece = undefined;
    }

    private updateMoveList(move: Move) {
        this.moveList.update(list => {
            if (list.length == 0) {
                return [[move.toAlgebraicNotation()]];
            }

            const newList = [...list];
            const lastRow = newList[newList.length - 1];

            if (move instanceof PromotionMove) {
                lastRow.pop();
                lastRow.push(move.toAlgebraicNotation());
            }
            else if (lastRow.length < 2) {
                lastRow.push(move.toAlgebraicNotation());
            }
            else {
                newList.push([move.toAlgebraicNotation()]);
            }

            return newList;
        });
    }

    closeModal() {
        this.modalService.dismissAll();
    }

    private updateBoardView(move: Move, animate: boolean) {
        this.clearHighlights();

        if (animate) {
            const from = move.getFrom();
            const to = move.getTo();

            const boardRect = this.boardElementRef()?.nativeElement.getBoundingClientRect();

            const source = {
                x: boardRect.left + from.col * this.SQUARE_WIDTH,
                y: boardRect.top + from.row * this.SQUARE_HEIGHT
            }

            const target = {
                x: boardRect.left + to.col * this.SQUARE_WIDTH,
                y: boardRect.top + to.row * this.SQUARE_HEIGHT
            };

            this.squares[from.row][from.col].translate(target.x - source.x, target.y - source.y, () => {
                const color = move.getColor();
                const checkStatus = move.getCheckStatus();

                const sp = this.chessboard!.getPiece(from.row, from.col);
                const tp = this.chessboard!.getPiece(to.row, to.col);

                const ss = this.squares[from.row][from.col];
                const ts = this.squares[to.row][to.col];

                if (sp == null) {
                    ss.clearPiece();
                } else {
                    ss.setPiece(sp.getType(), sp.getColor());
                }

                if (tp == null) {
                    ts.clearPiece();
                } else {
                    ts.setPiece(tp.getType(), tp.getColor());
                }

                ss.highlightMove();
                ts.highlightMove();

                if (move instanceof EnPassantMove) {
                    const cp = (move as EnPassantMove).getCapturedPawnPosition();
                    this.squares[cp.row][cp.col].clearPiece();
                }

                if (checkStatus != null) {
                    const { row, col } = this.chessboard!.findKing(color === "BLACK" ? "WHITE" : "BLACK");
                    this.squares[row][col].highlightCheck();
                }
            });

            if (move instanceof CastlingMove) {
                const rs = move.getRookPosition();
                const rt = move.getRookTargetPosition();

                const source = {
                    x: boardRect.left + rs.col * this.SQUARE_WIDTH,
                    y: boardRect.top + rs.row * this.SQUARE_HEIGHT
                }

                const target = {
                    x: boardRect.left + rt.col * this.SQUARE_WIDTH,
                    y: boardRect.top + rt.row * this.SQUARE_HEIGHT
                };

                this.squares[rs.row][rs.col].translate(target.x - source.x, target.y - source.y, () => {
                    const sp = this.chessboard!.getPiece(rs.row, rs.col);
                    const tp = this.chessboard!.getPiece(rt.row, rt.col);

                    const ss = this.squares[rs.row][rs.col];
                    const ts = this.squares[rt.row][rt.col];

                    if (sp == null) {
                        ss.clearPiece();
                    } else {
                        ss.setPiece(sp.getType(), sp.getColor());
                    }

                    if (tp == null) {
                        ts.clearPiece();
                    } else {
                        ts.setPiece(tp.getType(), tp.getColor());
                    }

                    ss.highlightMove();
                    ts.highlightMove();
                });
            }
        }
        else {
            const from = move.getFrom();
            const to = move.getTo();

            const sp = this.chessboard!.getPiece(from.row, from.col);
            const tp = this.chessboard!.getPiece(to.row, to.col);

            const ss = this.squares[from.row][from.col];
            const ts = this.squares[to.row][to.col];

            if (sp == null) {
                ss.clearPiece();
            } else {
                ss.setPiece(sp.getType(), sp.getColor());
            }

            if (tp == null) {
                ts.clearPiece();
            } else {
                ts.setPiece(tp.getType(), tp.getColor());
            }

            ss.resetPiecePosition();
            ss.highlightMove();
            ts.highlightMove();

            if (move instanceof EnPassantMove) {
                const cp = (move as EnPassantMove).getCapturedPawnPosition();
                this.squares[cp.row][cp.col].clearPiece();
            }

            if (move instanceof CastlingMove) {
                const rs = move.getRookPosition();
                const rt = move.getRookTargetPosition();

                const sp = this.chessboard!.getPiece(rs.row, rs.col);
                const tp = this.chessboard!.getPiece(rt.row, rt.col);

                const ss = this.squares[rs.row][rs.col];
                const ts = this.squares[rt.row][rt.col];

                if (sp == null) {
                    ss.clearPiece();
                } else {
                    ss.setPiece(sp.getType(), sp.getColor());
                }

                if (tp == null) {
                    ts.clearPiece();
                } else {
                    ts.setPiece(tp.getType(), tp.getColor());
                }

                ss.highlightMove();
                ts.highlightMove();
            }
        }
    }

    gameOver(): boolean {
        return this.gameState() !== "ACTIVE" && this.gameState() !== "AWAITING_PROMOTION";
    }

    private initMoveList(moves: Move[]): void {
        const list = [];
        for (let i = 0; i < moves.length; i += 2) {
            const row = [];
            row.push(moves[i].toAlgebraicNotation());
            if (i + 1 < moves.length) {
                row.push(moves[i + 1].toAlgebraicNotation());
            }
            list.push(row);
        }

        this.moveList.set(list);
    }

    private initPlayers(playerData: PlayerData[]): void {
        const p = playerData.map(p => this.userApi.fetch(p.id));

        forkJoin(p).subscribe(result => {
            const players = [];
            for (let i = 0; i < result.length; i++) {
                players.push({
                    id: result[i].id,
                    displayName: result[i].displayName,
                    avatar: result[i].avatar,
                    color: playerData[i].color
                });
            }

            if (players[0].id !== this.chessService.getCurrentUserId()) {
                const tmp = players[0];
                players[0] = players[1];
                players[1] = tmp;
            }

            this.players.set(players);
        })
    }

    isDraw(): boolean {
        return this.gameState() === "STALEMATE" || this.gameState() === "THREEFOLD_REPETITION" || this.gameState() === "FIFTY_MOVE_RULE" || this.gameState() === "DEAD_POSITION";
    }

    private initBoardView(board: Array<Piece | null>): void {
        for (let i = 0; i < Chessboard.SIZE; i++) {
            for (let j = 0; j < Chessboard.SIZE; j++) {
                const piece = board[i * Chessboard.SIZE + j];
                if (piece == null) {
                    this.squares[i][j].clearPiece();
                }
                else {
                    this.squares[i][j].setPiece(piece.getType(), piece.getColor());
                }
            }
        }
    }

    startDragging(e: MouseEvent, i: number, j: number): void {
        if (this.activeColor() == null || this.activeColor() !== this.myColor()) {
            return;
        }

        const square = this.squares[i][j];
        if (square.getColor() !== this.myColor()) {
            return;
        }

        this.draggedPiece = { i, j, x: 0, y: 0 };
        this.draggedPiece.x = e.clientX - Square.WIDTH / 2;
        this.draggedPiece.y = e.clientY - Square.HEIGHT / 2;

        this.highlightLegalMoves(i, j);

        this.squares[i][j].moveTo(this.draggedPiece.x, this.draggedPiece.y);

        document.addEventListener("mousemove", this.dragPiece);
        document.addEventListener("mouseup", this.stopDragging);
    }

    dragPiece = (e: MouseEvent): void => {
        if (this.draggedPiece == null) {
            return;
        }

        const { i, j } = this.draggedPiece;

        this.draggedPiece.x += e.movementX;
        this.draggedPiece.y += e.movementY;

        this.squares[i][j].moveTo(this.draggedPiece.x, this.draggedPiece.y);
    };

    stopDragging = (e: MouseEvent): void => {
        this.unhighlithLegalMoves();

        const { top, left } = this.boardElementRef()?.nativeElement.getBoundingClientRect();

        const targetRow = Math.floor((e.clientY - top) / Square.HEIGHT);
        const targetCol = Math.floor((e.clientX - left) / Square.WIDTH);

        const { i, j } = this.draggedPiece!;

        if (targetRow < 0 || targetCol < 0 || targetRow >= this.BOARD_SIZE || targetCol >= this.BOARD_SIZE) {
            this.squares[i][j].resetPiecePosition();
        }
        else {
            this.chessService.move({ row: i, col: j }, { row: targetRow, col: targetCol });
        }

        document.removeEventListener("mousemove", this.dragPiece);
        document.removeEventListener("mouseup", this.stopDragging);
    }

    isEnabled(square: Square): boolean {
        return this.activeColor() !== undefined && this.activeColor() === this.myColor() && square.getColor() === this.myColor() && this.gameState() == "ACTIVE";
    }

    private highlightLegalMoves(row: number, col: number): void {
        const piece = this.chessboard!.getPiece(row, col);
        if (piece == null) {
            return;
        }

        const moves = piece.getLegalMoves(this.chessboard!, row, col);
        for (const move of moves) {
            const { row, col } = move;
            this.squares[row][col].highlightLegalMove();
        }
    }

    private unhighlithLegalMoves(): void {
        for (const row of this.squares) {
            for (const square of row) {
                square.unhighlightLegalMove();
            }
        }
    }

    private clearHighlights(): void {
        for (const row of this.squares) {
            for (const square of row) {
                square.clearHighlight();
            }
        }
    }

    getImg(color: Color, piece: PieceType,): string {
        return getPieceImg(color, piece);
    }

    promote(piece: PieceType): void {
        this.chessService.promote(piece);
    }

    resign(): void {
        this.chessService.resign();
    }

    show(modal: TemplateRef<any>): void {
        this.modalService.open(modal);
    }
}