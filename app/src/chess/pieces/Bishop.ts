import { Chessboard } from "../Chessboard";
import { Move } from "../moves/Move";
import { AbstractPiece } from "./AbstractPiece";
import { BishopBehavior } from "./behavior/BishopBehavior";
import { Color } from "./Piece";

export class Bishop extends AbstractPiece {

    private behavior = new BishopBehavior();

    constructor(color: Color, moveCount?: number) {
        super(color, "BISHOP", moveCount);
    }

    getLegalMovesImpl(board: Chessboard, row: number, col: number): Move[] {
        return this.behavior.getMoves(board, this.getType(), this.getColor(), row, col);
    }

    override toString(): string {
        return this.getColor() == "WHITE" ? String.fromCodePoint(9815) : String.fromCodePoint(9821);
    }
}
