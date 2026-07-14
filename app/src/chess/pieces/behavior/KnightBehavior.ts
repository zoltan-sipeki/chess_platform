import { Chessboard } from "../../Chessboard";
import { createBasicMove, Move } from "../../moves/Move";
import { Color } from "../AbstractPiece";
import { Knight } from "../Knight";
import { Piece, PieceType } from "../Piece";
import { PieceBehavior } from "./PieceBahavior";

export class KnightBehavior implements PieceBehavior {

    getMoves(board: Chessboard, piece: PieceType | null, color: Color, row: number, col: number): Move[] {
        const moveList: Move[] = [];

        const upperSecondRow = row - 2 >= 0;
        const upperFirstRow = row - 1 >= 0;
        const lowerFirstRow = row + 1 < Chessboard.SIZE;
        const lowerSecondRow = row + 2 < Chessboard.SIZE;
        const leftSecondCol = col - 2 >= 0;
        const leftFirstCol = col - 1 >= 0;
        const rightFirstCol = col + 1 < Chessboard.SIZE;
        const rightSecondCol = col + 2 < Chessboard.SIZE;

        if (upperSecondRow) {
            if (leftFirstCol) {
                const move = createBasicMove(board, { row, col }, { row: row - 2, col: col - 1 }, piece, color);
                if (move != null) {
                    moveList.push(move);
                }
            }
            if (rightFirstCol) {
                const move = createBasicMove(board, { row, col }, { row: row - 2, col: col + 1 }, piece, color);
                if (move != null) {
                    moveList.push(move);
                }
            }
        }

        if (upperFirstRow) {
            if (leftSecondCol) {
                const move = createBasicMove(board, { row, col }, { row: row - 1, col: col - 2 }, piece, color);
                if (move != null) {
                    moveList.push(move);
                }
            }
            if (rightSecondCol) {
                const move = createBasicMove(board, { row, col }, { row: row - 1, col: col + 2 }, piece, color);
                if (move != null) {
                    moveList.push(move);
                }
            }
        }

        if (lowerFirstRow) {
            if (leftSecondCol) {
                const move = createBasicMove(board, { row, col }, { row: row + 1, col: col - 2 }, piece, color);
                if (move != null) {
                    moveList.push(move);
                }
            }
            if (rightSecondCol) {
                const move = createBasicMove(board, { row, col }, { row: row + 1, col: col + 2 }, piece, color);
                if (move != null) {
                    moveList.push(move);
                }
            }
        }

        if (lowerSecondRow) {
            if (leftFirstCol) {
                const move = createBasicMove(board, { row, col }, { row: row + 2, col: col - 1 }, piece, color);
                if (move != null) {
                    moveList.push(move);
                }
            }
            if (rightFirstCol) {
                const move = createBasicMove(board, { row, col }, { row: row + 2, col: col + 1 }, piece, color);
                if (move != null) {
                    moveList.push(move);
                }
            }
        }

        return moveList;
    }

    canBeAppliedTo(piece: Piece | null): boolean {
        return piece instanceof Knight;
    }

}