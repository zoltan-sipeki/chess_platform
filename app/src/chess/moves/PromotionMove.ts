import { Chessboard } from "../Chessboard";
import { Color, Piece, PieceType } from "../pieces/Piece";
import { AbstractMove } from "./AbstractMove";
import { CheckStatus, Move, MoveType, Position } from "./Move";

export class PromotionMove implements Move {

    private move: AbstractMove;

    private promotedPieceInstance: Piece;

    private replay: boolean;

    constructor(move: AbstractMove, promotedPieceInstance: Piece, replay: boolean) {
        this.move = move;
        this.promotedPieceInstance = promotedPieceInstance;
        this.replay = replay;
    }

    validate(board: Chessboard): boolean {
        return true;
    }

    execute(board: Chessboard): void {
        if (this.replay) {
            this.executeFirstHalf(board);
            this.executeSecondHalf(board);
        }
        else {
            this.executeSecondHalf(board);
        }
    }

    getFrom(): Position {
        return this.move.getFrom();
    }

    getPiece(): PieceType | null {
        return this.move.getPiece();
    }

    getColor(): Color {
        return this.move.getColor();
    }

    getTo(): Position {
        return this.move.getTo();
    }

    undo(board: Chessboard): void {
        if (this.replay) {
            this.undoSecondHalf(board);
            this.undoFirstHalf(board);
        }
        else {
            this.undoSecondHalf(board);
        }
    }

    getCheckStatus(): CheckStatus | undefined {
        return this.move.getCheckStatus();
    }

    getTimestamp(): number | undefined {
        return this.move.getTimestamp();
    }

    getType(): MoveType {
        return this.move.getType();
    }

    isPromotion(): boolean {
        return true;
    }

    toAlgebraicNotation(): string {
        const notation = `${this.move.toAlgebraicNotation()}=${this.promotedPieceInstance.toString()}`;
        const checkStatus = this.getCheckStatus();
        if (checkStatus === "CHECK") {
            return notation.replace("+", "") + "+";
        }

        if (checkStatus === "CHECKMATE") {
            return notation.replace("++", "") + "++";
        }

        return notation;
    }

    private executeFirstHalf(board: Chessboard): void {
        this.move.execute(board);
    }

    private executeSecondHalf(board: Chessboard): void {
        const to = this.getTo();
        board.setPiece(to.row, to.col, this.promotedPieceInstance!);
    }

    private undoFirstHalf(board: Chessboard): void {
        this.move.undo(board);
    }

    private undoSecondHalf(board: Chessboard): void {
        const to = this.getTo();
        board.setPiece(to.row, to.col, this.move.getMovedPieceInstance()!);
    }
}