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

    private BASE_URL = "/api/matchmaking";

    private http: HttpClient = inject(HttpClient);

    enqueue(queueType: QueueType): Observable<void> {
        return this.http.post<void>(`${this.BASE_URL}/queues/${queueType}/members`, {});
    }

    dequeue(): Observable<void> {
        return this.http.delete<void>(`${this.BASE_URL}/queues/members/me`);
    }

    invite(id: string): Observable<void> {
        return this.http.post<void>(`${this.BASE_URL}/private-match`, { inviteeId: id });
    }

    fetchCurrentMatch(): Observable<CurrentMatch> {
        return this.http.get<CurrentMatch>(`${this.BASE_URL}/current-match`);
    }

    declineCurrentMatch(): Observable<void> {
        return this.http.delete<void>(`${this.BASE_URL}/current-match`);
    }

}