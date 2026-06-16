import { HttpClient } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { Observable } from "rxjs";
import { UserData } from "./UserApi";

export type FriendRequestStatus = "ACCEPTED" | "REJECTED";

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

    sendRequest(userId: string): Observable<UserData | null> {
        return this.http.post<UserData | null>("/api/friend-requests", { receiverId: userId });
    }

    updateRequest(friendRequestId: string, update: FriendRequestUpdate): Observable<UserData | null> {
        return this.http.patch<UserData | null>(`/api/friend-requests/${friendRequestId}`, update);
    }
}