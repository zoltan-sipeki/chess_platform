import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { UserData } from "./UserApi";

export type QueueType = "ranked" | "unranked";

export type MatchStatus = "PENDING" | "ACTIVE";

export interface CurrentMatch {
    target: string,
    status: MatchStatus,
    inviter?: UserData,
    invitee?: UserData
    token?: string
}

@Injectable({
    providedIn: 'root'
})
export class MatchmakingApi {

    private http: HttpClient = inject(HttpClient);

    enqueue(queueType: QueueType): Observable<void> {
        return this.http.post<void>(`/api/matchmaking/queues/${queueType}/members`, {});
    }

    dequeue(): Observable<void> {
        return this.http.delete<void>(`/api/matchmaking/queues/members/me`);
    }

    invite(id: string): Observable<void> {
        return this.http.post<void>(`/api/matchmaking/private-match`, { inviteeId: id });
    }

    fetchCurrentMatch(): Observable<CurrentMatch> {
        return this.http.get<CurrentMatch>("/api/matchmaking/current-match");
    }

    declineCurrentMatch(): Observable<void> {
        return this.http.delete<void>("/api/matchmaking/current-match");
    }

}