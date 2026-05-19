import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";

export type Color = "WHITE" | "BLACK";

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
    joinedAt: Date,
    lastPlayedAt: Date
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
    rankedMmr: number,
    percentile: number
}

export interface MatchHistory {
    matchId: string,
    matchType: MatchType,
    startedAt: Date,
    duration: number,
    color: Color,
    score: Score,
    mmrChange: number
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

@Injectable({ providedIn: 'root' })
export class MatchService {

    private http: HttpClient = inject(HttpClient);

    fetchLeaderboard(): Promise<LeaderboardEntry[]> {
        return new Promise(resolve => {
            this.http.get<LeaderboardEntry[]>("/api/leaderboard").subscribe({
                next: result => resolve(result),
                error: err => console.error(err)
            });
        });
    }

    fetchMatchHistory(userId: string, query: MatchHistoryQuery): Observable<MatchHistoryList> {
        return this.http.get<MatchHistoryList>("/api/matches", { params: { userId, ...query } });
    }
}