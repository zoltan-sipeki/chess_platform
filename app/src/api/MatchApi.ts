import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Color } from "../chess/pieces/Piece";
import { MoveData } from "../services/ChessService";

export type Score = "WIN" | "LOSS" | "DRAW";

export type MatchType = "RANKED" | "UNRANKED" | "PRIVATE";

export interface Player {
    id: string,
    displayName: string,
    avatar: string
}

export interface LongestStreak {
    score: string,
    longestStreak: number
}

export interface PlayerStats {
    rank: number,
    mmr: number,
    percentile: number,
    longestStreaks: LongestStreak[],
    joinedAt: string,
    lastPlayedAt: string
}

export interface MatchStat {
    userId: string,
    matchType: MatchType,
    gamesPlayed: number,
    wins: number,
    losses: number,
    draws: number,
    winRatio: number
}

export interface LeaderboardEntry {
    player: Player,
    rank: number,
    mmr: number,
    percentile: number
}

export interface MatchHistory {
    matchId: string,
    matchType: MatchType,
    startedAt: string,
    duration: number,
    color: Color,
    outcome: Score,
    mmrChange?: number
}

export interface MatchHistoryList {
    total: number,
    matches: MatchHistory[]
}

export interface MatchHistoryQuery {
    sort?: string,
    page?: number,
    size?: number,
    matchType?: string,
    outcome?: string
}

export interface ReplayPlayer {
    id: string,
    color: Color,
    mmrBefore?: number,
    mmrAfter?: number,
    score: number
}

export interface MatchReplay {
    matchId: number,
    matchType: MatchType,
    startedAt: string,
    endedAt: string,
    players: ReplayPlayer[],
    replay: MoveData[]
}

@Injectable({ providedIn: 'root' })
export class MatchApi {

    private BASE_URL = "/api/matches";

    private http: HttpClient = inject(HttpClient);

    fetchLeaderboard(): Observable<LeaderboardEntry[]> {
        return this.http.get<LeaderboardEntry[]>("/api/leaderboard");
    }

    fetchMatchHistory(userId: string, query: MatchHistoryQuery): Observable<MatchHistoryList> {
        return this.http.get<MatchHistoryList>(`${this.BASE_URL}`, { params: { userId, ...query } });
    }

    fetchMatchStats(userId: string): Observable<MatchStat[]> {
        return this.http.get<MatchStat[]>(`${this.BASE_URL}/stats`, { params: { userId } });
    }

    fetchPlayerStats(userId: string): Observable<PlayerStats> {
        return this.http.get<PlayerStats>(`/api/players/${userId}/stats`);
    }

    fetchReplay(matchId: string): Observable<MatchReplay> {
        return this.http.get<MatchReplay>(`${this.BASE_URL}/${matchId}/replay`);
    }
}