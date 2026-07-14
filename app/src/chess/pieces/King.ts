import { Chessboard } from "../Chessboard";
import { CastlingMove } from "../moves/CastlingMove";
import { Move } from "../moves/Move";
import { AbstractPiece, Color } from "./AbstractPiece";
import { KingBasicBehavior } from "./behavior/KingBasicBehavior";
import { Rook } from "./Rook";

export class King extends AbstractPiece {

    private basicMoves = new KingBasicBehavior();

    constructor(color: Color, moveCount?: number) {
        super(color, "KING", moveCount);
    }

    getLegalMovesImpl(board: Chessboard, row: number, col: number): Move[] {
        const moveList = this.basicMoves.getMoves(board, this.getType(), this.getColor(), row, col);
        if (this.canCastle(board, row, col, Chessboard.LEFT_ROOK_COL)) {
            moveList.push(new CastlingMove(this.getType(), this.getColor(), { row, col }, { row, col: 2 }));
        } else if (this.canCastle(board, row, col, Chessboard.RIGHT_ROOK_COL)) {
            moveList.push(new CastlingMove(this.getType(), this.getColor(), { row, col }, { row, col: Chessboard.SIZE - 2 }));
        }

        return moveList;
    }

    private canCastle(board: Chessboard, row: number, col: number, rookCol: number): boolean {
        if (this.hasMoved()) {
            return false;
        }

        const dir = rookCol == Chessboard.LEFT_ROOK_COL ? -1 : 1;

        const rook = board.getPiece(row, rookCol);
        if (!(rook instanceof Rook) || rook?.hasMoved()) {
            return false;
        }

        for (let j = col + dir; Math.abs(j - rookCol) > 0; j += dir) {
            if (board.getPiece(row, j) != null) {
                return false;
            }
        }

        return true;
    }

    override toString(): string {
        return this.getColor() == "WHITE" ? String.fromCodePoint(9812) : String.fromCodePoint(9818);
    }

}