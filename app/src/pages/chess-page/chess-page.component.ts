import { TitleCasePipe } from "@angular/common";
import { Component, inject, OnDestroy, Signal, signal, TemplateRef, viewChild } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { NgbModal } from "@ng-bootstrap/ng-bootstrap";
import { forkJoin } from 'rxjs';
import { UserApi } from "../../api/UserApi";
import { getPieceImg, Square, SquareAnimation, Squares } from "../../chess/chess-view";
import { Chessboard } from "../../chess/Chessboard";
import { Move } from "../../chess/moves/Move";
import { PromotionMove } from "../../chess/moves/PromotionMove";
import { Color, Piece, PieceType } from "../../chess/pieces/Piece";
import { Moves, Pieces } from "../../chess/utils";
import { AvatarComponent } from "../../components/avatar/avatar.component";
import { ChessboardComponent, PieceDragEvent, Player } from "../../components/chess-board/chess-board.component";
import { SnakeCaseToTitleCasePipe } from "../../pipes/SnakeCaseToTitleCase";
import { ChessService, GameState, MatchSnapshot, MoveResult, PlayerData, ScoreboardPlayerData } from "../../services/ChessService";
import { CountDownService } from "../../services/CountDownService";

interface DraggedPiece {
    i: number,
    j: number,
    x: number,
    y: number
}

interface ScoreboardPlayer {
    id: string,
    displayName: string,
    avatar: string,
    color: Color,
    mmrBefore?: number,
    mmrAfter?: number,
    score: number
}

@Component({
    selector: 'chess-page',
    templateUrl: 'chess-page.component.html',
    standalone: true,
    imports: [TitleCasePipe, AvatarComponent, SnakeCaseToTitleCasePipe, ChessboardComponent],
    providers: [CountDownService]
})
export class ChessPage implements OnDestroy {

    private userApi: UserApi = inject(UserApi);

    private chessService: ChessService = inject(ChessService);

    private countDownService: CountDownService = inject(CountDownService);

    private route: ActivatedRoute = inject(ActivatedRoute);

    private modalService: NgbModal = inject(NgbModal);

    private board = viewChild(ChessboardComponent);

    private scoreboardTemplateRef = viewChild("scoreboardModal", { read: TemplateRef });

    private draggedPiece?: DraggedPiece;

    readonly SQUARE_WIDTH: number = Square.WIDTH;

    readonly SQUARE_HEIGHT: number = Square.HEIGHT;

    private chessboard?: Chessboard;

    promotablePieces: PieceType[] = ["QUEEN", "ROOK", "BISHOP", "KNIGHT"];

    squares: Square[][];

    players = signal<Player[]>([]);

    scoreboard = signal<ScoreboardPlayer[]>([]);

    moves = signal<string[][]>([]);

    myColor = signal<Color | undefined>(undefined);

    winnerColor = signal<Color | undefined>(undefined);

    activeColor = signal<Color | undefined>(undefined);

    gameState = signal<GameState | undefined>(undefined);

    countDown: Signal<number>;

    constructor() {
        this.squares = Squares.create();
        this.countDown = this.countDownService.countDown;
        this.chessService.subscribe("AUTHENTICATED", this.joinMatch);
        this.chessService.subscribe("MATCH_SNAPSHOT", this.initialize);
        this.chessService.subscribe("MOVE_RESULT", this.move);
        this.chessService.connect(this.route.snapshot.params["target"]);
    }
    
    ngOnDestroy(): void {
        this.chessService.unsubscribe("AUTHENTICATED", this.joinMatch);
        this.chessService.unsubscribe("MATCH_SNAPSHOT", this.initialize);
        this.chessService.unsubscribe("MOVE_RESULT", this.move);
    }

    myTurn(): boolean {
        return this.activeColor() === this.myColor();
    }

    isDraw(): boolean {
        return this.gameState() === "STALEMATE" || this.gameState() === "THREEFOLD_REPETITION" || this.gameState() === "FIFTY_MOVE_RULE" || this.gameState() === "DEAD_POSITION";
    }

    startDragging({ mouseEvent, i, j }: PieceDragEvent): void {
        if (this.activeColor() == null || this.activeColor() !== this.myColor()) {
            return;
        }

        const square = this.squares[i][j];
        if (square.getColor() !== this.myColor()) {
            return;
        }

        this.draggedPiece = { i, j, x: 0, y: 0 };
        this.draggedPiece.x = mouseEvent.clientX - Square.WIDTH / 2;
        this.draggedPiece.y = mouseEvent.clientY - Square.HEIGHT / 2;

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
        Squares.unhighlightLegalMoves(this.squares);

        const { top, left } = this.board()?.getBoundingClientRect()!;

        const targetRow = Math.floor((e.clientY - top) / Square.HEIGHT);
        const targetCol = Math.floor((e.clientX - left) / Square.WIDTH);

        const { i, j } = this.draggedPiece!;

        if (targetRow < 0 || targetCol < 0 || targetRow >= Chessboard.SIZE || targetCol >= Chessboard.SIZE) {
            this.squares[i][j].resetPiecePosition();
        }
        else {
            this.chessService.move({ row: i, col: j }, { row: targetRow, col: targetCol });
        }

        document.removeEventListener("mousemove", this.dragPiece);
        document.removeEventListener("mouseup", this.stopDragging);
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

    closeModal() {
        this.modalService.dismissAll();
    }

    private joinMatch = (): void => {
        const token = localStorage.getItem("matchmakingToken");
        localStorage.removeItem("matchmakingToken");

        this.chessService.joinMatch(token);
    }

    private initialize = (snapshot: MatchSnapshot): void => {
        const { activeColor, moves, board, players, nextTurn, state } = snapshot;

        const moveList = moves.map(m => Moves.reconstruct(m));
        const pieces = board.map(p => p == null ? null : Pieces.reconstruct(p));

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
            const m = Moves.reconstruct(move);
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
                    players.push({
                        ...player,
                        score: p.score,
                        mmrBefore: p.mmrBefore,
                        mmrAfter: p.mmrAfter
                    })
                    break;
                }
            }
        }

        this.scoreboard.set(players);
    }

    private myMove(move: Move): boolean {
        return move.getColor() === this.myColor();
    }


    private resetDraggedPiece(): void {
        const { i, j } = this.draggedPiece!;
        this.squares[i][j].resetPiecePosition();
        this.draggedPiece = undefined;
    }

    private updateMoveList(move: Move) {
        this.moves.update(list => {
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


    private updateBoardView(move: Move, animate: boolean) {
        Squares.clearHighlights(this.squares);

        if (animate) {
            SquareAnimation.execute(move, this.squares, this.chessboard!, this.board()!.getBoundingClientRect());
        }
        else {
            Squares.showMove(move, this.squares, this.chessboard!);
            Squares.highlightCheck(move, this.squares, this.chessboard!);
        }
    }

    private gameOver(): boolean {
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

        this.moves.set(list);
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

    private highlightLegalMoves(row: number, col: number): void {
        const piece = this.chessboard!.getPiece(row, col);
        if (piece == null) {
            return;
        }

        const moves = piece.getLegalMoves(this.chessboard!, row, col);
        Squares.highlightLegalMoves(moves, this.squares!);
    }
}