import { AsyncPipe } from "@angular/common";
import { Component, inject, signal, viewChild } from "@angular/core";
import { FormControl, ReactiveFormsModule } from "@angular/forms";
import { ActivatedRoute } from "@angular/router";
import { forkJoin, Observable, shareReplay } from 'rxjs';
import { MatchApi, MatchReplay } from "../../api/MatchApi";
import { UserApi } from "../../api/UserApi";
import { Square, SquareAnimation, Squares } from "../../chess/chess-view";
import { Chessboard } from "../../chess/Chessboard";
import { Move } from "../../chess/moves/Move";
import { Color, Piece } from "../../chess/pieces/Piece";
import { Moves, Pieces } from "../../chess/utils";
import { ChessboardComponent, Player } from "../../components/chess-board/chess-board.component";
import { PlayerData } from "../../services/ChessService";

@Component({
    selector: 'replay-page',
    templateUrl: 'replay-page.component.html',
    standalone: true,
    imports: [ChessboardComponent, ReactiveFormsModule, AsyncPipe]
})
export class ReplayPage {

    private matchApi: MatchApi = inject(MatchApi);

    private userApi: UserApi = inject(UserApi);

    private route: ActivatedRoute = inject(ActivatedRoute);

    private board = viewChild(ChessboardComponent);

    private startedAt: number = 0;

    replay$!: Observable<MatchReplay>;

    chessboard!: Chessboard;

    activeColor = signal<Color | undefined>(undefined);

    squares: Square[][];

    moves!: Move[];

    moveCount = signal<number>(0);

    moveList = signal<string[][]>([]);

    timer = signal<number>(0);

    players = signal<Player[]>([]);

    cursor = new FormControl(0, { nonNullable: true });

    private oldCursor: number = 0;

    constructor() {
        this.squares = Squares.create();
        this.cursor.valueChanges.subscribe(cursor => {
            this.skip(this.oldCursor, cursor);
            this.oldCursor = cursor;
        });

        this.route.params.subscribe(params => {
            const matchId = params["id"];
            if (matchId == null) {
                return;
            }

            this.replay$ = this.matchApi.fetchReplay(matchId).pipe(shareReplay(1));

            this.replay$.subscribe(result => {
                const { startedAt, replay, players } = result;

                const moveList = replay.map(move => Moves.reconstruct(move, true));
                const pieces = Pieces.createDefaultLayout();

                this.activeColor.set(moveList[0] ? moveList[0].getColor() : undefined);
                this.startedAt = new Date(startedAt).getTime();
                this.chessboard = new Chessboard(moveList, pieces);
                this.moveCount.set(moveList.length);
                this.moves = moveList;
                this.initBoardView(pieces);
                this.initPlayers(players);

            })
        })
    }

    shiftCursor(n: number): void {
        let newCursor;
        if (n > 0) {
            newCursor = Math.min(this.cursor.value + n, this.moves.length);
        }
        else {
            newCursor = Math.max(this.cursor.value + n, 0);
        }

        this.cursor.setValue(newCursor);
    }

    skip(from: number, to: number): void {
        if (from === to) {
            return;
        }

        if (to - from > 0) {
            for (let i = from; i < to; ++i) {
                Squares.clearHighlights(this.squares);

                const move = this.moves[i];
                move.execute(this.chessboard!);

                this.pushMove(move);
                this.timer.set(move.getTimestamp()! - this.startedAt);

                SquareAnimation.execute(move, this.squares, this.chessboard!, this.board()!.getBoundingClientRect());
            }

            if (to < this.moves.length) {
                this.activeColor.set(this.moves[to].getColor());
            }
            else {
                this.activeColor.set(undefined);
            }
        }
        else {
            for (let i = from - 1; i >= to && i >= 0; i--) {
                Squares.clearHighlights(this.squares);

                const move = this.moves[i];
                move.undo(this.chessboard!);

                this.popMove();

                SquareAnimation.undo(move, this.squares, this.chessboard!, this.board()!.getBoundingClientRect());
            }

            if (to > 0) {
                Squares.highlightMove(this.moves[to - 1], this.squares, this.chessboard!);
            }

            this.activeColor.set(this.moves[to].getColor());

            if (to > 0) {
                this.timer.set(this.moves[to - 1].getTimestamp()! - this.startedAt);
            }
            else {
                this.timer.set(0);
            }
        }
    }

    skipForward(): void {
        this.shiftCursor(this.moves.length - this.cursor.value);
    }

    skipBackward(): void {
        this.shiftCursor(-this.cursor.value);
    }

    private initPlayers(playerData: PlayerData[]): void {
        forkJoin(playerData.map(p => this.userApi.fetch(p.id))).subscribe(result => {
            const ans = [];
            for (let i = 0; i < result.length; i++) {
                ans.push({
                    id: result[i].id,
                    displayName: result[i].displayName,
                    avatar: result[i].avatar,
                    color: playerData[i].color
                });
            }

            this.players.set(ans);
        });
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

    private pushMove(move: Move) {
        this.moveList.update(list => {
            if (list.length == 0) {
                return [[move.toAlgebraicNotation()]];
            }

            const newList = [...list];
            const lastRow = newList[newList.length - 1];

            if (lastRow.length < 2) {
                lastRow.push(move.toAlgebraicNotation());
            }
            else {
                newList.push([move.toAlgebraicNotation()]);
            }

            return newList;
        });
    }

    private popMove(): void {
        this.moveList.update(list => {
            const newList = [...list];
            const lastRow = newList[newList.length - 1];
            if (lastRow.length > 1) {
                lastRow.pop();
            }
            else {
                newList.pop();
            }
            return newList;
        });
    }
}