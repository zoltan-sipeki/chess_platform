import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";

export type QueueType = "ranked" | "unranked";

export interface MatchmakingToken {
    token: string
}

@Injectable({
    providedIn: 'root'
})
export class QueueApi {

    private http: HttpClient = inject(HttpClient);

    enqueue(queueType: QueueType): Observable<void> {
        return this.http.post<void>(`/api/queues/${queueType}/members`, {});
    }

    dequeue(): Observable<void> {
        return this.http.delete<void>(`/api/queues/members/me`);
    }

    invite(id: string): Observable<void> {
        return this.http.post<void>(`/api/queues/private`, { inviteeId: id });
    }

}