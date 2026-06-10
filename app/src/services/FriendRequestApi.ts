import { HttpClient } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { Observable } from "rxjs";
import { UserData } from "../types";

export type FriendRequestStatus = "ACCEPTED" | "REJECTED";

export interface FriendRequestCreate {
    receiverId: string
}

export interface FriendRequestUpdate {
    status: FriendRequestStatus
}

export interface FriendRequest {
    id: string;
    sender: UserData;
}


@Injectable({
    providedIn: "root",
})
export class FriendRequestApi {

    private http: HttpClient = inject(HttpClient);

    fetchAll(): Observable<FriendRequest[]> {
        return this.http.get<FriendRequest[]>("/api/friend-requests");
    }

    sendRequest(userId: string, body: FriendRequestCreate): Observable<void> {
        return this.http.post<void>("/api/friend-requests", body);
    }

    updateRequest(friendRequestId: string, update: FriendRequestUpdate): Observable<UserData | null> {
        return this.http.patch<UserData | null>(`/api/friend-requests/${friendRequestId}`, update);
    }
}