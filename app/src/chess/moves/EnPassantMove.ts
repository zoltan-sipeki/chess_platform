import { toAlgebraicNotation, toCodePoint } from "../chess-view";
import { Chessboard } from "../Chessboard";
import { Color } from "../pieces/AbstractPiece";
import { Piece, PieceType } from "../pieces/Piece";
import { AbstractMove } from "./AbstractMove";
import { CheckStatus, Position } from "./Move";

export class EnPassantMove extends AbstractMove {

    private capturedPawnInstance?: Piece;

    constructor(piece: PieceType | null, color: Color, from: Position, to: Position, checkStatus?: CheckStatus, timestamp?: number) {
        super(piece, color, from, to, "EN_PASSANT", checkStatus, timestamp);
    }

    override execute(board: Chessboard): void {
        super.execute(board);

        const movedPiece = this.getMovedPieceInstance()!;
        const from = this.getFrom();
        const to = this.getTo();

        const capturedPos = this.getCapturedPawnPosition();
        this.capturedPawnInstance = board.getPiece(capturedPos.row, capturedPos.col)!;

        board.setPiece(to.row, to.col, movedPiece);
        board.setPiece(from.row, from.col, null);
        board.setPiece(capturedPos.row, capturedPos.col, null);
    }

    override undo(board: Chessboard): void {
        const movedPiece = this.getMovedPieceInstance()!;
        const capturedPos = this.getCapturedPawnPosition();
        const from = this.getFrom();
        const to = this.getTo();

        board.setPiece(from.row, from.col, movedPiece);
        board.setPiece(to.row, to.col, null);
        board.setPiece(capturedPos.row, capturedPos.col, this.capturedPawnInstance!);

        this.capturedPawnInstance = undefined;

        super.undo(board);
    }

    getCapturedPawnPosition(): Position {
        const to = this.getTo();
        return { row: to.row - Chessboard.getPawnDirection(this.getColor()), col: to.col };
    }

    toAlgebraicNotationImpl(): string {
        return `${toCodePoint(this.getColor() + "-" + this.getPiece())}${toAlgebraicNotation(this.getFrom())}x${toAlgebraicNotation(this.getTo())}e.p.`;
    }

}