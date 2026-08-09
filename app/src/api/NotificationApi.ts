import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { Observable } from "rxjs";
import { UserData } from "./UserApi";

export type NotificationType = "FRIEND_REQUEST" | "FRIEND_REQUEST_ACCEPTED";

export interface NotificationList {
    unread: number,
    lastReadSeq: number,
    notifications: Notification[],
    last: number
}

export interface Notification {
    id: string,
    seq: number,
    type: NotificationType
    sender: UserData,
    createdAt: string,
    friendRequest?: string
}

export interface NotificationUpdate {
    lastReadSequenceNumber: number
}

export interface NotificationQuery {
    before?: number,
    limit?: number
}

@Injectable({
    providedIn: 'root'
})
export class NotificationApi {

    private BASE_URL = "/api/notifications";

    private http: HttpClient = inject(HttpClient);

    fetchAll(query: NotificationQuery): Observable<NotificationList> {
        let params = new HttpParams();
        for (const [key, value] of Object.entries(query)) {
            if (value != null) {
               params = params.append(key, value);
            }
        }
        return this.http.get<NotificationList>(`${this.BASE_URL}`, { params });
    }

    delete(id: string): Observable<void> {
        return this.http.delete<void>(`${this.BASE_URL}/${id}`);
    }

    deleteAll(): Observable<void> {
        return this.http.delete<void>(`${this.BASE_URL}`);
    }

    updateAll(update: NotificationUpdate): Observable<void> {
        return this.http.patch<void>(`${this.BASE_URL}`, update);
    }
}