import { Chessboard } from "../../Chessboard";
import { CaptureMove } from "../../moves/CaptureMove";
import { createBasicMove, Move } from "../../moves/Move";
import { Color } from "../AbstractPiece";
import { Piece, PieceType } from "../Piece";
import { Queen } from "../Queen";
import { Rook } from "../Rook";
import { PieceBehavior } from "./PieceBahavior";

export class RookBehavior implements PieceBehavior {

    getMoves(board: Chessboard, piece: PieceType | null, color: Color, row: number, col: number): Move[] {
        const moveList: Move[] = [];

        for (let j = col + 1; j < Chessboard.SIZE; ++j) {
            const move = createBasicMove(board, { row, col }, { row, col: j }, piece, color);
            if (move != null) {
                moveList.push(move);
            }

            if (move == null || move instanceof CaptureMove) {
                break;
            }
        }

        for (let j = col - 1; j >= 0; --j) {
            const move = createBasicMove(board, { row, col }, { row, col: j }, piece, color);
            if (move != null) {
                moveList.push(move);
            }

            if (move == null || move instanceof CaptureMove) {
                break;
            }
        }

        for (let i = row + 1; i < Chessboard.SIZE; ++i) {
            const move = createBasicMove(board, { row, col }, { row: i, col }, piece, color);
            if (move != null) {
                moveList.push(move);
            }

            if (move == null || move instanceof CaptureMove) {
                break;
            }
        }

        for (let i = row - 1; i >= 0; --i) {
            const move = createBasicMove(board, { row, col }, { row: i, col }, piece, color);
            if (move != null) {
                moveList.push(move);
            }

            if (move == null || move instanceof CaptureMove) {
                break;
            }
        }

        return moveList;
    }

    canBeAppliedTo(piece: Piece | null): boolean {
        return piece instanceof Rook || piece instanceof Queen;
    }


}