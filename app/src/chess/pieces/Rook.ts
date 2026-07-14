import { Chessboard } from "../Chessboard";
import { Move } from "../moves/Move";
import { AbstractPiece, Color } from "./AbstractPiece";
import { RookBehavior } from "./behavior/RookBehavior";

export class Rook extends AbstractPiece {

    private behavior = new RookBehavior();

    constructor(color: Color, moveCount?: number) {
        super(color, "ROOK", moveCount);
    }

    getLegalMovesImpl(board: Chessboard, row: number, col: number): Move[] {
        return this.behavior.getMoves(board, this.getType(), this.getColor(), row, col);
    }

    override toString(): string {
        return this.getColor() == "WHITE" ? String.fromCodePoint(9814) : String.fromCodePoint(9820);
    }
}