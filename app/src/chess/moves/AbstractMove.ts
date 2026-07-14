import { Chessboard } from "../Chessboard";
import { Color } from "../pieces/AbstractPiece";
import { Piece, PieceType } from "../pieces/Piece";
import { CheckStatus, Move, MoveType, Position } from "./Move";

export abstract class AbstractMove implements Move {

    private from: Position;

    private to: Position;

    private piece: PieceType | null;

    private color: Color;

    private type: MoveType;

    private checkStatus?: CheckStatus;

    private timestamp?: number;

    private movedPiece?: Piece;

    constructor(piece: PieceType | null, color: Color, from: Position, to: Position, type: MoveType, checkStatus?: CheckStatus, timestamp?: number) {
        this.piece = piece;
        this.color = color;
        this.from = from;
        this.to = to;
        this.type = type;
        this.checkStatus = checkStatus;
        this.timestamp = timestamp;
    }

    toAlgebraicNotation(): string {
        const notation = this.toAlgebraicNotationImpl();
        if (this.checkStatus === "CHECK") {
            return notation + "+";
        }

        if (this.checkStatus === "CHECKMATE") {
            return notation + "++";
        }

        return notation;
    }

    abstract toAlgebraicNotationImpl(): string;

    validate(board: Chessboard): boolean {
        this.execute(board);
        const isKingInCheck = board.isKingInCheck(this.getColor());
        this.undo(board);
        return !isKingInCheck;
    }

    getPiece(): PieceType | null {
        return this.piece;
    }

    getColor(): Color {
        return this.color;
    }

    execute(board: Chessboard): void {
        const { from } = this;
        this.movedPiece = board.getPiece(from.row, from.col)!;
        this.movedPiece.incrementMoveCount();
    }

    undo(board: Chessboard): void {
        this.movedPiece!.decrementMoveCount();
    }

    getFrom(): Position {
        return this.from;
    }

    getTo(): Position {
        return this.to;
    }

    getCheckStatus(): CheckStatus | undefined {
        return this.checkStatus;
    }

    getTimestamp(): number | undefined {
        return this.timestamp;
    }

    getType(): MoveType {
        return this.type;
    }

    getMovedPieceInstance(): Piece | undefined {
        return this.movedPiece;
    }

}