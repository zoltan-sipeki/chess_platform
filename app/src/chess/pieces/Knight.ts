import { Chessboard } from "../Chessboard";
import { Move } from "../moves/Move";
import { AbstractPiece } from "./AbstractPiece";
import { KnightBehavior } from "./behavior/KnightBehavior";
import { Color } from "./Piece";

export class Knight extends AbstractPiece {

    private behavior = new KnightBehavior();

    constructor(color: Color, moveCount?: number) {
        super(color, "KNIGHT", moveCount);
    }

    getLegalMovesImpl(board: Chessboard, row: number, col: number): Move[] {
        return this.behavior.getMoves(board, this.getType(), this.getColor(), row, col);
    }

    override toString(): string {
        return this.getColor() == "WHITE" ? String.fromCodePoint(9816) : String.fromCodePoint(9822);
    }
}