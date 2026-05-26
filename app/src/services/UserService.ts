import { HttpClient, HttpEvent } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable, shareReplay } from "rxjs";
import { UserData, UserSearchResult } from "../types";
import { FriendList } from "./FriendService";
import { MatchHistoryList, MatchStat, PlayerStats } from "./MatchService";

export type Relationship = "FRIENDS" | "SELF" | "NOT_RELATED";

export type PrivacySetting = "PUBLIC" | "PRIVATE" | "FRIENDS";

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

export interface PrivacySettings {
    friends?: PrivacySetting,
    playerStats?: PrivacySetting,
    matchStats?: PrivacySetting,
    matchHistory?: PrivacySetting
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


    fetchPrivacy(): Observable<PrivacySettings> {
        return this.http.get<PrivacySettings>("/api/privacy").pipe(shareReplay(1));
    }

    fetchPrivacyEvents(): Observable<HttpEvent<Object>> {
        return this.http.get("/api/privacy", { observe: "events", reportProgress: true }).pipe(shareReplay(1));
    }

    updateChatPrivacy(settings: PrivacySettings): Observable<PrivacySettings> {
        return this.http.patch<PrivacySettings>("/api/privacy/chat", settings);
    }

    updateMatchPrivacy(settings: PrivacySettings): Observable<PrivacySettings> {
        return this.http.patch<PrivacySettings>("/api/privacy/match", settings);
    }
}