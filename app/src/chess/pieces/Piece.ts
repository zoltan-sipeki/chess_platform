import { Chessboard } from "../Chessboard";
import { Position } from "../moves/Move";

export type Color = "WHITE" | "BLACK";

export type PieceType = "PAWN" | "KNIGHT" | "BISHOP" | "ROOK" | "QUEEN" | "KING";

export interface Piece {

    getColor(): Color;

    getType(): PieceType;

    getLegalMoves(board: Chessboard, row: number, col: number): Position[];

    hasMoved(): boolean;

    incrementMoveCount(): void;

    decrementMoveCount(): void;

    getName(): string;
}