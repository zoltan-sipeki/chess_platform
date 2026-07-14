import { toAlgebraicNotation, toCodePoint } from "../chess-view";
import { Chessboard } from "../Chessboard";
import { Color } from "../pieces/AbstractPiece";
import { PieceType } from "../pieces/Piece";
import { AbstractMove } from "./AbstractMove";
import { CheckStatus, Position } from "./Move";

export class SimpleMove extends AbstractMove {

    constructor(piece: PieceType | null, color: Color, from: Position, to: Position, checkStatus?: CheckStatus, timestamp?: number) {
        super(piece, color, from, to, "SIMPLE", checkStatus, timestamp);
    }

    override execute(board: Chessboard): void {
        super.execute(board);
        const from = this.getFrom();
        const to = this.getTo();

        board.setPiece(to.row, to.col, this.getMovedPieceInstance()!);
        board.setPiece(from.row, from.col, null);
    }

    override undo(board: Chessboard): void {
        const from = this.getFrom();
        const to = this.getTo();

        board.setPiece(from.row, from.col, this.getMovedPieceInstance()!);
        board.setPiece(to.row, to.col, null);
        super.undo(board);
    }

    toAlgebraicNotationImpl(): string {
        return `${toCodePoint(this.getColor() + "-" + this.getPiece())}${toAlgebraicNotation(this.getFrom())}-${toAlgebraicNotation(this.getTo())}`
    }
}