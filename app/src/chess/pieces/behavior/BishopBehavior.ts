import { Chessboard } from "../../Chessboard";
import { CaptureMove } from "../../moves/CaptureMove";
import { createBasicMove, Move } from "../../moves/Move";
import { Color } from "../AbstractPiece";
import { Bishop } from "../Bishop";
import { Piece, PieceType } from "../Piece";
import { Queen } from "../Queen";
import { PieceBehavior } from "./PieceBahavior";

export class BishopBehavior implements PieceBehavior {

    canBeAppliedTo(piece: Piece | null): boolean {
        return piece instanceof Bishop || piece instanceof Queen;
    }

    getMoves(board: Chessboard, piece: PieceType | null, color: Color, row: number, col: number): Move[] {
        const moveList: Move[] = [];

        for (let i = row + 1, j = col + 1; i < Chessboard.SIZE && j < Chessboard.SIZE; ++i, ++j) {
            const move = createBasicMove(board, { row, col }, { row: i, col: j }, piece, color);
            if (move != null) {
                moveList.push(move);
            }

            if (move == null || move instanceof CaptureMove) {
                break;
            }
        }

        for (let i = row + 1, j = col - 1; i < Chessboard.SIZE && j >= 0; ++i, --j) {
            const move = createBasicMove(board, { row, col }, { row: i, col: j }, piece, color);
            if (move != null) {
                moveList.push(move);
            }

            if (move == null || move instanceof CaptureMove) {
                break;
            }
        }

        for (let i = row - 1, j = col - 1; i >= 0 && j >= 0; --i, --j) {
            const move = createBasicMove(board, { row, col }, { row: i, col: j }, piece, color);
            if (move != null) {
                moveList.push(move);
            }

            if (move == null || move instanceof CaptureMove) {
                break;
            }
        }

        for (let i = row - 1, j = col + 1; i >= 0 && j < Chessboard.SIZE; --i, ++j) {
            const move = createBasicMove(board, { row, col }, { row: i, col: j }, piece, color);
            if (move != null) {
                moveList.push(move);
            }

            if (move == null || move instanceof CaptureMove) {
                break;
            }
        }

        return moveList;
    }
}
