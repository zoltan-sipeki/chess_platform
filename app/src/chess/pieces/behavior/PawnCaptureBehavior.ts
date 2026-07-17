import { Chessboard } from "../../Chessboard";
import { CaptureMove } from "../../moves/CaptureMove";
import { Move } from "../../moves/Move";
import { King } from "../King";
import { Pawn } from "../Pawn";
import { Color, Piece, PieceType } from "../Piece";
import { PieceBehavior } from "./PieceBahavior";

export class PawnCaptureBehavior implements PieceBehavior {

    getMoves(board: Chessboard, piece: PieceType | null, color: Color, row: number, col: number): Move[] {
        const moveList: Move[] = [];

        const dir = Chessboard.getPawnDirection(color);
        const targetRow = row + dir;

        if (targetRow < 0 || targetRow >= Chessboard.SIZE) {
            return moveList;
        }

        if (col - 1 >= 0) {
            const target = board.getPiece(targetRow, col - 1);
            if (isValidCapture(target, color)) {
                const move = new CaptureMove(piece, color, { row, col }, { row: targetRow, col: col - 1 });
                moveList.push(move);
            }
        }
        if (col + 1 < Chessboard.SIZE) {
            const target = board.getPiece(targetRow, col + 1);
            if (isValidCapture(target, color)) {
                const move = new CaptureMove(piece, color, { row, col }, { row: targetRow, col: col + 1 });
                moveList.push(move);
            }
        }

        return moveList;
    }

    canBeAppliedTo(piece: Piece | null): boolean {
        return piece instanceof Pawn;
    }

}

function isValidCapture(target: Piece | null, color: Color): boolean {
    return target != null && target.getColor() != color && !(target instanceof King);
}