import { MoveData, PieceData } from "../services/ChessService";
import { Chessboard } from "./Chessboard";
import { CaptureMove } from "./moves/CaptureMove";
import { CastlingMove } from "./moves/CastlingMove";
import { EnPassantMove } from "./moves/EnPassantMove";
import { Move, Position } from "./moves/Move";
import { PromotionMove } from "./moves/PromotionMove";
import { SimpleMove } from "./moves/SimpleMove";
import { Bishop } from "./pieces/Bishop";
import { King } from "./pieces/King";
import { Knight } from "./pieces/Knight";
import { Pawn } from "./pieces/Pawn";
import { Color, Piece, PieceType } from "./pieces/Piece";
import { Queen } from "./pieces/Queen";
import { Rook } from "./pieces/Rook";

export class Moves {

    static reconstruct(data: MoveData, replay: boolean = false) {
        const { type, piece, color, from, to, checkStatus, timestamp, promotedPiece } = data;

        let move;

        switch (type) {
            case "SIMPLE":
                move = new SimpleMove(piece, color, from, to, checkStatus, timestamp);
                break;
            case "CAPTURE":
                move = new CaptureMove(piece, color, from, to, checkStatus, timestamp);
                break;
            case "EN_PASSANT":
                move = new EnPassantMove(piece, color, from, to, checkStatus, timestamp);
                break;
            case "QUEENSIDE_CASTLING":
                move = new CastlingMove(piece, color, from, to, checkStatus, timestamp);
                break;
            case "KINGSIDE_CASTLING":
                move = new CastlingMove(piece, color, from, to, checkStatus, timestamp);
                break;
        }

        if (promotedPiece != null) {
            move = new PromotionMove(move, Pieces.reconstruct(promotedPiece), replay);
        }

        return move;
    }

    static createBasic(board: Chessboard, from: Position, to: Position, piece: PieceType | null, color: Color): Move | null {
        const target = board.getPiece(to.row, to.col);

        if (target == null) {
            return new SimpleMove(piece, color, from, to);
        }

        if (target.getColor() != color && !(target instanceof King)) {
            return new CaptureMove(piece, color, from, to);
        }

        return null;
    }
}

export class Pieces {

    static reconstruct(data: PieceData) {
        const { type, color, moveCount } = data;
        return Pieces.create(type, color, moveCount);
    }

    static create(type: PieceType, color: Color, moveCount?: number): Piece {
        switch (type) {
            case "PAWN": {
                return new Pawn(color, moveCount);
            }
            case "KNIGHT": {
                return new Knight(color, moveCount);
            }
            case "BISHOP": {
                return new Bishop(color, moveCount);
            }
            case "ROOK": {
                return new Rook(color, moveCount);
            }
            case "QUEEN": {
                return new Queen(color, moveCount);
            }
            case "KING": {
                return new King(color, moveCount);
            }
        };
    }

    static createDefaultLayout(): Array<Piece | null> {
        const board: Array<Piece | null> = new Array(Chessboard.SIZE * Chessboard.SIZE);

        for (let i = 0; i < Chessboard.SIZE; ++i) {
            for (let j = 0; j < Chessboard.SIZE; ++j) {
                if (i == 1) {
                    board[i * Chessboard.SIZE + j] = Pieces.create("PAWN", "BLACK");
                } else if (i == 6) {
                    board[i * Chessboard.SIZE + j] = Pieces.create("PAWN", "WHITE");
                } else if (i > 1 && i < 6) {
                    board[i * Chessboard.SIZE + j] = null;
                }
            }
        }

        board[0 * Chessboard.SIZE + 0] = Pieces.create("ROOK", "BLACK");
        board[0 * Chessboard.SIZE + 1] = Pieces.create("KNIGHT", "BLACK");
        board[0 * Chessboard.SIZE + 2] = Pieces.create("BISHOP", "BLACK");
        board[0 * Chessboard.SIZE + 3] = Pieces.create("QUEEN", "BLACK");
        board[0 * Chessboard.SIZE + 4] = Pieces.create("KING", "BLACK");
        board[0 * Chessboard.SIZE + 5] = Pieces.create("BISHOP", "BLACK");
        board[0 * Chessboard.SIZE + 6] = Pieces.create("KNIGHT", "BLACK");
        board[0 * Chessboard.SIZE + 7] = Pieces.create("ROOK", "BLACK");

        board[7 * Chessboard.SIZE + 0] = Pieces.create("ROOK", "WHITE");
        board[7 * Chessboard.SIZE + 1] = Pieces.create("KNIGHT", "WHITE");
        board[7 * Chessboard.SIZE + 2] = Pieces.create("BISHOP", "WHITE");
        board[7 * Chessboard.SIZE + 3] = Pieces.create("QUEEN", "WHITE");
        board[7 * Chessboard.SIZE + 4] = Pieces.create("KING", "WHITE");
        board[7 * Chessboard.SIZE + 5] = Pieces.create("BISHOP", "WHITE");
        board[7 * Chessboard.SIZE + 6] = Pieces.create("KNIGHT", "WHITE");
        board[7 * Chessboard.SIZE + 7] = Pieces.create("ROOK", "WHITE");

        return board;
    }
}