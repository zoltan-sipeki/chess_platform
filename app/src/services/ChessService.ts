import { Injectable, inject } from "@angular/core";
import { CheckStatus, MoveType } from "../chess/moves/Move";
import { Color } from "../chess/pieces/AbstractPiece";
import { PieceType } from "../chess/pieces/Piece";
import { AuthService } from "./AuthService";

export type ChessEventType = "MATCH_SNAPSHOT" | "MOVE_RESULT" | "AUTHENTICATED";

export type GameState = "ACTIVE" | "AWAITING_PROMOTION" | "CHECKMATE" | "STALEMATE" | "DEAD_POSITION" | "THREEFOLD_REPETITION" | "FIFTY_MOVE_RULE" | "FLAG_FALL" | "RESIGNATION";

export interface PlayerData {
    id: string,
    color: Color,
}

export interface ScoreboardPlayerData {
    id: string,
    color: Color,
    mmrBefore?: number,
    mmrAfter?: number,
    score: number
}

export interface PositionData {
    row: number,
    col: number
}

export interface PromotedPieceData {
    color: Color,
    type: PieceType
}

export interface MoveData {
    from: PositionData,
    to: PositionData,
    type: MoveType,
    piece: PieceType,
    color: Color,
    checkStatus?: CheckStatus,
    promotedPiece?: PromotedPieceData
}

export interface PieceData {
    color: Color,
    type: PieceType,
    moveCount?: number
}

export interface MatchSnapshot {
    nextTurn: number,
    activeColor: Color,
    state: GameState,
    players: PlayerData[],
    moves: MoveData[],
    board: PieceData[]
}

export interface MoveResult {
    activeColor?: Color,
    nextTurn: number,
    move?: MoveData,
    state: GameState,
    scoreboard?: ScoreboardPlayerData[],
    winnerColor?: Color
}

export interface EventMap {
    "MATCH_SNAPSHOT": MatchSnapshot;
    "MOVE_RESULT": MoveResult;
    "AUTHENTICATED": void;
}

@Injectable({
    providedIn: 'root'
})
export class ChessService {

    private ws?: WebSocket;

    private authService: AuthService = inject(AuthService);

    private listeners = new Map<ChessEventType, Function[]>();

    subscribe<T extends ChessEventType>(eventType: T, callback: (event: EventMap[T]) => void): void {
        const listeners = this.listeners.get(eventType);
        if (!listeners) {
            this.listeners.set(eventType, [callback]);
        } else {
            listeners.push(callback);
        }
    }

    unsubscribe(eventType: ChessEventType, callback: Function): void {
        const listeners = this.listeners.get(eventType);
        if (listeners) {
            this.listeners.set(eventType, listeners.filter(l => l !== callback));
        }
    }

    connect(target: string): void {
        this.ws = new WebSocket(`ws://localhost:8081/chess/ws?target=${target}`);
        this.ws.onopen = async () => {
            const accessToken = await this.authService.getAccessToken();
            if (accessToken != null) {
                this.authenticate(accessToken);
            }
        };

        this.ws.onmessage = async e => {
            const { type, payload } = JSON.parse(e.data);
            switch (type) {
                case "ERROR": {
                    console.error(payload);
                    break;
                }

                default: {
                    const listeners = this.listeners.get(type);
                    if (listeners) {
                        for (const listener of listeners) {
                            listener(payload);
                        }
                    }
                    break;
                }
            }
        }
    }

    private send(obj: any): void {
        this.ws?.send(JSON.stringify(obj));
    }

    authenticate(accessToken: string): void {
        this.send({ type: "AUTHENTICATE", payload: { accessToken } });
    }

    joinMatch(token: string | null): void {
        this.send({ type: "JOIN_MATCH", payload: { token } });
    }

    move(from: PositionData, to: PositionData): void {
        this.send({ type: "MOVE", payload: { from, to } });
    }

    promote(promotedPiece: PieceType): void {
        this.send({ type: "PROMOTION", payload: { promotedPiece } });
    }

    resign(): void {
        this.send({ type: "RESIGN", payload: {} });
    }

    getCurrentUserId(): string {
        return this.authService.getUserId();
    }

}