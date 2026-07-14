import { Chessboard } from "../Chessboard";
import { Color } from "../pieces/AbstractPiece";
import { King } from "../pieces/King";
import { PieceType } from "../pieces/Piece";
import { CaptureMove } from "./CaptureMove";
import { SimpleMove } from "./SimpleMove";

export type CheckStatus = "CHECK" | "CHECKMATE";

export type MoveType = "SIMPLE" | "EN_PASSANT" | "QUEENSIDE_CASTLING" | "KINGSIDE_CASTLING" | "CAPTURE";

export interface Position {
    row: number;
    col: number;
}

export interface Move {

    validate(board: Chessboard): boolean;

    execute(board: Chessboard): void;

    undo(board: Chessboard): void;

    getPiece(): PieceType | null;

    getColor(): Color;

    getFrom(): Position;

    getTo(): Position;

    getCheckStatus(): CheckStatus | undefined;

    getTimestamp(): number | undefined;

    getType(): MoveType;

    toAlgebraicNotation(): string;
}

export function createBasicMove(board: Chessboard, from: Position, to: Position, piece: PieceType | null, color: Color): Move | null {
    const target = board.getPiece(to.row, to.col);

    if (target == null) {
        return new SimpleMove(piece, color, from, to);
    }

    if (target.getColor() != color && !(target instanceof King)) {
        return new CaptureMove(piece, color, from, to);
    }

    return null;
}