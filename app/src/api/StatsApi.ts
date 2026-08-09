import { HttpClient } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { Observable } from "rxjs";
import { LongestStreak, MatchType } from "./MatchApi";

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

@Injectable({ providedIn: 'root' })
export class StatsApi {

    private BASE_URL = "/api/stats";

    private http: HttpClient = inject(HttpClient);

    fetchMatchStats(userId: string): Observable<MatchStat[]> {
        return this.http.get<MatchStat[]>(`${this.BASE_URL}/match`, { params: { userId } });
    }

    fetchPlayerStats(userId: string): Observable<PlayerStats> {
        return this.http.get<PlayerStats>(`${this.BASE_URL}/player`, { params: { userId } });
    }

}