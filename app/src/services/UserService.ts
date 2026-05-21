import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { UserData, UserSearchResult } from "../types";
import { FriendList } from "./FriendService";
import { MatchHistoryList, MatchStat, PlayerStats } from "./MatchService";

export type Relationship = "FRIENDS" | "SELF" | "NOT_RELATED";

export interface UserProfile {
    user: UserData,
    relationship: Relationship,
    playerStats?: PlayerStats,
    matches: MatchHistoryList,
    matchStats: MatchStat[],
    friends: FriendList
}

export interface OngoingMatch {
    matchId: number,
    userId: string,
    target: string
}

export interface Dashboard {
    user: UserData,
    friends: FriendList,
    channels: any[],
    ongoingMatch: OngoingMatch
}

@Injectable({ providedIn: 'root' })
export class UserService {

    private http: HttpClient = inject(HttpClient);

    constructor() { }

    findUsersByDisplayNamePrefix(prefix: string, limit: number = 5): Promise<UserSearchResult> {
        if (prefix.length === 0) {
            return Promise.resolve({ hasMore: false, users: [] });
        }

        return new Promise<UserSearchResult>((resolve, reject) => {
            this.http.get<UserSearchResult>("/api/users", {
                params: {
                    startsWith: prefix,
                    limit
                }
            }).subscribe({
                next: result => resolve(result),
                error: err => reject(err)
            });
        });
    }

    fetchProfile(userId: string): Observable<UserProfile> {
        return this.http.get<UserProfile>(`/api/profiles/${userId}`);
    }

    fetchDashboard(): Observable<Dashboard> {
        return this.http.get<Dashboard>("/api/dashboard");
    }
}