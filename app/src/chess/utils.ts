import { GameState, MoveData, PieceData } from "../services/ChessService";
import { CaptureMove } from "./moves/CaptureMove";
import { CastlingMove } from "./moves/CastlingMove";
import { EnPassantMove } from "./moves/EnPassantMove";
import { Move } from "./moves/Move";
import { PromotionMove } from "./moves/PromotionMove";
import { SimpleMove } from "./moves/SimpleMove";
import { Bishop } from "./pieces/Bishop";
import { King } from "./pieces/King";
import { Knight } from "./pieces/Knight";
import { Pawn } from "./pieces/Pawn";
import { Piece } from "./pieces/Piece";
import { Queen } from "./pieces/Queen";
import { Rook } from "./pieces/Rook";

export function reconstructMove(data: MoveData, state: GameState, replay: boolean = false): Move {
    const { type, piece, color, from, to, checkStatus, promotedPiece } = data;

    let move;

    switch (type) {
        case "SIMPLE":
            move = new SimpleMove(piece, color, from, to, checkStatus);
            break;
        case "CAPTURE":
            move = new CaptureMove(piece, color, from, to, checkStatus);
            break;
        case "EN_PASSANT":
            move = new EnPassantMove(piece, color, from, to, checkStatus);
            break;
        case "QUEENSIDE_CASTLING":
            move = new CastlingMove(piece, color, from, to, checkStatus);
            break;
        case "KINGSIDE_CASTLING":
            move = new CastlingMove(piece, color, from, to, checkStatus);
            break;
    }

    if (promotedPiece != null) {
        move = new PromotionMove(move, reconstructPiece(promotedPiece), replay);
    }

    return move;
}

export function reconstructPiece(data: PieceData): Piece {
    const { type, color, moveCount } = data;

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