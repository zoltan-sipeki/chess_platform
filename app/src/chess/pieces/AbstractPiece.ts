import { Chessboard } from "../Chessboard";
import { Move, Position } from "../moves/Move";
import { Color, Piece, PieceType } from "./Piece";

export abstract class AbstractPiece implements Piece {

    private color: Color;

    private moveCount: number;

    private type: PieceType;

    constructor(color: Color, type: PieceType, moveCount?: number) {
        this.color = color;
        this.type = type;
        this.moveCount = moveCount ?? 0;
    }

    getType(): PieceType {
        return this.type;
    }

    hasMoved(): boolean {
        return this.moveCount > 0;
    }

    getLegalMoves(board: Chessboard, row: number, col: number): Position[] {
        const moves = this.getLegalMovesImpl(board, row, col);
        const result: Position[] = [];

        for (const move of moves) {
            if (move.validate(board)) {
                result.push(move.getTo());
            }
        }

        return result;
    }

    abstract getLegalMovesImpl(board: Chessboard, row: number, col: number): Move[];

    getColor(): Color {
        return this.color;
    }

    incrementMoveCount(): void {
        ++this.moveCount;
    }

    decrementMoveCount(): void {
        --this.moveCount;
    }

    getName(): string {
        return `${this.color}-${this.type}`;
    }

}