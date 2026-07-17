import { Chessboard } from "../../Chessboard";
import { Move } from "../../moves/Move";
import { Color, Piece, PieceType } from "../Piece";

export interface PieceBehavior {

    getMoves(board: Chessboard, piece: PieceType | null, color: Color, row: number, col: number): Move[];

    canBeAppliedTo(piece: Piece | null): boolean;
}