import { Chessboard } from "../Chessboard";
import { King } from "../pieces/King";
import { Color, PieceType } from "../pieces/Piece";
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

