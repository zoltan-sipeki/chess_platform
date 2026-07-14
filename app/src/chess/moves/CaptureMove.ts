import { toAlgebraicNotation, toCodePoint } from "../chess-view";
import { Chessboard } from "../Chessboard";
import { Color } from "../pieces/AbstractPiece";
import { Piece, PieceType } from "../pieces/Piece";
import { AbstractMove } from "./AbstractMove";
import { CheckStatus, Position } from './Move';

export class CaptureMove extends AbstractMove {

    private capturedPiece?: Piece;

    constructor(piece: PieceType | null, color: Color, from: Position, to: Position, checkStatus?: CheckStatus, timestamp?: number) {
        super(piece, color, from, to, "CAPTURE", checkStatus, timestamp);
    }

    override execute(board: Chessboard): void {
        super.execute(board);

        const from = this.getFrom();
        const to = this.getTo();
        this.capturedPiece = board.getPiece(to.row, to.col)!;

        board.setPiece(to.row, to.col, this.getMovedPieceInstance()!);
        board.setPiece(from.row, from.col, null);
    }

    override undo(board: Chessboard): void {
        const from = this.getFrom();
        const to = this.getTo();

        board.setPiece(from.row, from.col, this.getMovedPieceInstance()!);
        board.setPiece(to.row, to.col, this.capturedPiece!);
        this.capturedPiece = undefined;

        super.undo(board);
    }

    toAlgebraicNotationImpl(): string {
        return `${toCodePoint(this.getColor() + "-" + this.getPiece())}${toAlgebraicNotation(this.getFrom())}x${toAlgebraicNotation(this.getTo())}`;
    }
}