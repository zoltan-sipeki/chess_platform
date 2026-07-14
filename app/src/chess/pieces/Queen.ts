import { Chessboard } from "../Chessboard";
import { Move } from "../moves/Move";
import { AbstractPiece, Color } from "./AbstractPiece";
import { BishopBehavior } from "./behavior/BishopBehavior";
import { RookBehavior } from "./behavior/RookBehavior";

export class Queen extends AbstractPiece {

    private rookBehavior = new RookBehavior();

    private bishopBehavior = new BishopBehavior();

    constructor(color: Color, moveCount?: number) {
        super(color, "QUEEN", moveCount);
    }

    getLegalMovesImpl(board: Chessboard, row: number, col: number): Move[] {
        return [
            ...this.rookBehavior.getMoves(board, this.getType(), this.getColor(), row, col),
            ...this.bishopBehavior.getMoves(board, this.getType(), this.getColor(), row, col)
        ];
    }

    override toString(): string {
        return this.getColor() == "WHITE" ? String.fromCodePoint(9813) : String.fromCodePoint(9819);
    }
}