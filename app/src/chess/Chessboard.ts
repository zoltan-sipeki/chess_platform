import { CaptureMove } from "./moves/CaptureMove";
import { Move, Position } from "./moves/Move";
import { PromotionMove } from "./moves/PromotionMove";
import { Color } from "./pieces/AbstractPiece";
import { BishopBehavior } from "./pieces/behavior/BishopBehavior";
import { KingBasicBehavior } from "./pieces/behavior/KingBasicBehavior";
import { KnightBehavior } from "./pieces/behavior/KnightBehavior";
import { PawnCaptureBehavior } from "./pieces/behavior/PawnCaptureBehavior";
import { PieceBehavior } from "./pieces/behavior/PieceBahavior";
import { RookBehavior } from "./pieces/behavior/RookBehavior";
import { King } from "./pieces/King";
import { Piece } from "./pieces/Piece";

export class Chessboard {

    static readonly SIZE: number = 8;

    static readonly LEFT_ROOK_COL: number = 0;

    static readonly RIGHT_ROOK_COL: number = 7;

    private board: Array<Piece | null> = new Array(Chessboard.SIZE * Chessboard.SIZE);

    private moves: Move[];

    constructor(moves: Move[], board: Array<Piece | null>) {
        this.moves = moves;
        this.board = board;
    }

    getBoard(): Array<Piece | null> {
        return this.board;
    }

    getPiece(i: number, j: number): Piece | null {
        return this.board[i * Chessboard.SIZE + j];
    }

    setPiece(i: number, j: number, piece: Piece | null): void {
        this.board[i * Chessboard.SIZE + j] = piece;
    }

    isKingInCheck(color: Color): boolean {
        const { row, col } = this.findKing(color);
        return this.isUnderAttack(row, col, color);
    }

    isUnderAttack(row: number, col: number, color: Color): boolean {
        const behaviors: PieceBehavior[] = [
            new PawnCaptureBehavior(),
            new KnightBehavior(),
            new KingBasicBehavior(),
            new RookBehavior(),
            new BishopBehavior()
        ];

        for (const behavior of behaviors) {
            if (this.isAttacked(row, col, color, behavior)) {
                return true;
            }
        }

        return false;
    }

    findKing(color: Color): Position {
        for (let i = 0; i < this.board.length; i++) {
            const piece = this.board[i];
            if (piece instanceof King && piece.getColor() == color) {
                return { row: Math.floor(i / Chessboard.SIZE), col: i % Chessboard.SIZE };
            }
        }
        return { row: -1, col: -1 };
    }

    getLastMove(): Move {
        return this.moves[this.moves.length - 1];
    }

    add(move: Move): void {
        if (move instanceof PromotionMove) {
            this.moves.pop();
        }
        
        this.moves.push(move);
    }

    private isAttacked(row: number, col: number, color: Color, behavior: PieceBehavior): boolean {
        const moveList = behavior.getMoves(this, null, color, row, col);
        for (const move of moveList) {
            if (!(move instanceof CaptureMove)) {
                continue;
            }

            const to = move.getTo();
            const capturedPiece = this.getPiece(to.row, to.col)!;
            if (capturedPiece.getColor() !== color
                && behavior.canBeAppliedTo(capturedPiece)) {
                return true;
            }
        }
        return false;
    }

    static getPawnDirection(color: Color): number {
        return color === "WHITE" ? -1 : 1;
    }

    static getPromotionRow(color: Color): number {
        return color === "WHITE" ? 0 : Chessboard.SIZE - 1;
    }

    static getEnPassantRow(color: Color): number {
        return color === "WHITE" ? 3 : 4;
    }

}