import { inject, Injectable } from "@angular/core";
import { catchError, forkJoin, map, Observable, of } from "rxjs";
import { FriendApi, FriendList } from "../api/FriendApi";
import { MatchApi, MatchHistoryList, MatchStat, PlayerStats } from "../api/MatchApi";
import { RelationshipApi } from "../api/RelationshipApi";
import { Relationship, UserApi, UserData } from "../api/UserApi";

export interface UserProfile {
    user: UserData,
    relationship: Relationship,
    playerStats?: PlayerStats,
    matches: MatchHistoryList,
    matchStats?: MatchStat[],
    friends: FriendList
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
            friends: this.friendApi.fetchAll(userId, { size: 10 }),
            playerStats: this.matchApi.fetchPlayerStats(userId).pipe(catchError(() => of(undefined))),
            matches: this.matchApi.fetchMatchHistory(userId, { size: 5 }),
            matchStats: this.matchApi.fetchMatchStats(userId).pipe(catchError(() => of(undefined)))
        });
    }

    fetchFriends(userId: string): Observable<FriendList> {
        return this.friendApi.fetchAll(userId, { size: 10 });
    }
}