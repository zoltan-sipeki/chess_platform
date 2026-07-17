import { Chessboard } from "../Chessboard";
import { Color, Piece, PieceType } from "../pieces/Piece";
import { AbstractMove } from "./AbstractMove";
import { CheckStatus, Position } from "./Move";

export class CastlingMove extends AbstractMove {

    private rook?: Piece;

    constructor(piece: PieceType | null, color: Color, from: Position, to: Position, checkStatus?: CheckStatus, timestamp?: number) {
        super(piece, color, from, to, isKingSide(from, to) ? "KINGSIDE_CASTLING" : "QUEENSIDE_CASTLING", checkStatus, timestamp);
    }

    override validate(board: Chessboard): boolean {
        const from = this.getFrom();
        const to = this.getTo();

        const rookPosition = this.getRookPosition();
        const dir = this.getType() == "KINGSIDE_CASTLING" ? 1 : -1;

        for (let j = from.col; Math.abs(j - rookPosition.col) > 0; j += dir) {
            if (board.isUnderAttack(to.row, j, this.getColor())) {
                return false;
            }
        }

        return true;
    }

    override execute(board: Chessboard): void {
        super.execute(board);

        const from = this.getFrom();
        const to = this.getTo();
        const rookPosition = this.getRookPosition();

        this.rook = board.getPiece(rookPosition.row, rookPosition.col)!;

        const movedPiece = this.getMovedPieceInstance()!;

        board.setPiece(to.row, to.col, movedPiece);
        board.setPiece(from.row, from.col, null);

        if (this.getType() == "QUEENSIDE_CASTLING") {
            board.setPiece(to.row, to.col + 1, this.rook);
        } else {
            board.setPiece(to.row, to.col - 1, this.rook);
        }

        board.setPiece(rookPosition.row, rookPosition.col, null);
    }

    override undo(board: Chessboard): void {
        const from = this.getFrom();
        const to = this.getTo();
        const movedPiece = this.getMovedPieceInstance()!;

        board.setPiece(to.row, to.col, null);
        board.setPiece(from.row, from.col, movedPiece);

        if (this.getType() == "QUEENSIDE_CASTLING") {
            board.setPiece(to.row, to.col + 1, null);
        } else {
            board.setPiece(to.row, to.col - 1, null);
        }

        const rookPosition = this.getRookPosition();

        board.setPiece(rookPosition.row, rookPosition.col, this.rook!);

        this.rook = undefined;

        super.undo(board);
    }

    getRookPosition(): Position {
        const from = this.getFrom();
        const to = this.getTo();

        return {
            row: to.row,
            col: isKingSide(from, to) ? Chessboard.RIGHT_ROOK_COL : Chessboard.LEFT_ROOK_COL
        };
    }

    getRookTargetPosition(): Position {
        const to = this.getTo();
        return {
            row: to.row,
            col: isKingSide(this.getFrom(), to) ? to.col - 1 : to.col + 1
        }
    }

    toAlgebraicNotationImpl(): string {
        return this.getType() === "KINGSIDE_CASTLING" ? "0-0" : "0-0-0";
    }
}

function isKingSide(from: Position, to: Position): boolean {
    return from.col < to.col;
}

