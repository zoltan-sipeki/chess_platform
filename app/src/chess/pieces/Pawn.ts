import { Chessboard } from "../Chessboard";
import { EnPassantMove } from "../moves/EnPassantMove";
import { Move } from "../moves/Move";
import { SimpleMove } from "../moves/SimpleMove";
import { AbstractPiece } from "./AbstractPiece";
import { PawnCaptureBehavior } from "./behavior/PawnCaptureBehavior";
import { Color } from "./Piece";

export class Pawn extends AbstractPiece {

    private captureMoves = new PawnCaptureBehavior();

    constructor(color: Color, moveCount?: number) {
        super(color, "PAWN", moveCount);
    }

    getLegalMovesImpl(board: Chessboard, row: number, col: number): Move[] {
        const color = this.getColor();

        const moveList = this.captureMoves.getMoves(board, this.getType(), color, row, col);

        const direction = Chessboard.getPawnDirection(color);

        if (this.canEnPassant(board, row, col, col + 1)) {
            moveList.push(new EnPassantMove(this.getType(), this.getColor(), { row, col }, { row: row + direction, col: col + 1 }));
        } else if (this.canEnPassant(board, row, col, col - 1)) {
            moveList.push(new EnPassantMove(this.getType(), this.getColor(), { row, col }, { row: row + direction, col: col - 1 }));
        }

        if (this.hasMoved()) {
            const targetRow = row + direction;

            const target = board.getPiece(targetRow, col);
            if (target == null) {
                const move = new SimpleMove(this.getType(), this.getColor(), { row, col }, { row: targetRow, col });
                moveList.push(move);
            }

        } else {
            const end = row + direction * 2;
            for (let i = row + direction; ; i += direction) {
                if (direction < 0 ? i < end : i > end) {
                    break;
                }
                const target = board.getPiece(i, col);
                if (target == null) {
                    const move = new SimpleMove(this.getType(), this.getColor(), { row, col }, { row: i, col });
                    moveList.push(move);
                } else {
                    break;
                }
            }
        }

        return moveList;
    }

    private canEnPassant(board: Chessboard, row: number, col: number, targetCol: number): boolean {
        const color = this.getColor();

        const enpassantRow = Chessboard.getEnPassantRow(color);

        if (row != enpassantRow) {
            return false;
        }

        const lastMove = board.getLastMove();

        if (lastMove.getPiece() != "PAWN") {
            return false;
        }

        if (lastMove.getColor() == color) {
            return false;
        }

        const lastFrom = lastMove.getFrom();
        const lastTo = lastMove.getTo();

        if (Math.abs(lastFrom.row - lastTo.row) != 2) {
            return false;
        }

        return lastTo.col == targetCol;
    }

    override toString(): string {
        return this.getColor() == "WHITE" ? String.fromCodePoint(9817) : String.fromCodePoint(9823);
    }
}