import { Chessboard } from "../Chessboard";
import { Position } from "../moves/Move";
import { Color } from "./AbstractPiece";

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