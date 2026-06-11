import { inject, Injectable } from "@angular/core";
import { catchError, forkJoin, map, Observable, of } from "rxjs";
import { FriendApi, FriendList } from "./FriendApi";
import { MatchApi, MatchHistoryList, MatchStat, PlayerStats } from "./MatchApi";
import { RelationshipApi } from "./RelationshipApi";
import { Relationship, UserApi, UserData } from "./UserApi";

export interface UserProfile {
    user: UserData,
    relationship: Relationship,
    playerStats?: PlayerStats,
    matches?: MatchHistoryList,
    matchStats?: MatchStat[],
    friends?: FriendList
}

@Injectable({
    providedIn: 'root'
})
export class ProfileService {

    private userApi: UserApi = inject(UserApi);

    private matchApi: MatchApi = inject(MatchApi);

    private friendApi: FriendApi = inject(FriendApi);

    private relationshipApi: RelationshipApi = inject(RelationshipApi);

    fetch(userId: string): Observable<UserProfile> {
        return forkJoin({
            user: this.userApi.fetch(userId),
            relationship: this.relationshipApi.fetch(userId).pipe(map(r => r.relationship)),
            friends: this.friendApi.fetchFriends(userId, { size: 10 }).pipe(catchError(() => of(undefined))),
            playerStats: this.matchApi.fetchPlayerStats(userId).pipe(catchError(() => of(undefined))),
            matchStats: this.matchApi.fetchMatchStats(userId).pipe(catchError(() => of(undefined))),
            matches: this.matchApi.fetchMatchHistory(userId, { size: 5 }).pipe(catchError(() => of(undefined))),
        });
    }
}