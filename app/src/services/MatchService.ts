import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";

export interface Player {
    id: string,
    displayName: string,
    avatar: string
}

export interface LeaderboardEntry {
    player: Player,
    rank: number,
    rankedMmr: number,
    percentile: number
}

@Injectable({ providedIn: 'root' })
export class MatchService {

    private http: HttpClient = inject(HttpClient);

    public fetchLeaderboard(): Promise<LeaderboardEntry[]> {
        return new Promise(resolve => {
            this.http.get<LeaderboardEntry[]>("/api/leaderboard").subscribe({
                next: result => resolve(result),
                error: err => console.error(err)
            });
        });
    }
}