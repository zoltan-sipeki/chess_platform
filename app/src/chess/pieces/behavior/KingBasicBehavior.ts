import { Chessboard } from "../../Chessboard";
import { createBasicMove, Move } from "../../moves/Move";
import { Color } from "../AbstractPiece";
import { King } from "../King";
import { Piece, PieceType } from "../Piece";
import { PieceBehavior } from "./PieceBahavior";

export class KingBasicBehavior implements PieceBehavior {

    getMoves(board: Chessboard, piece: PieceType | null, color: Color, row: number, col: number): Move[] {
        const moveList: Move[] = [];

        for (let i = row - 1; i <= row + 1; ++i) {
            for (let j = col - 1; j <= col + 1; ++j) {
                if (i == row && j == col) {
                    continue;
                }

                if (i < 0 || i >= Chessboard.SIZE || j < 0 || j >= Chessboard.SIZE || (i == row && j == col)) {
                    continue;
                }
                const move = createBasicMove(board, { row, col }, { row: i, col: j }, piece, color);
                if (move != null) {
                    moveList.push(move);
                }
            }
        }

        return moveList;
    }

    canBeAppliedTo(piece: Piece | null): boolean {
        return piece instanceof King;
    }
}