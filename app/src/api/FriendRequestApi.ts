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

    private BASE_URL = "/api/friend-requests";

    private http: HttpClient = inject(HttpClient);

    fetchAll(): Observable<FriendRequest[]> {
        return this.http.get<FriendRequest[]>(`${this.BASE_URL}`);
    }

    sendRequest(userId: string): Observable<UserData | null> {
        return this.http.post<UserData | null>(`${this.BASE_URL}`, { receiverId: userId });
    }

    updateRequest(friendRequestId: string, update: FriendRequestUpdate): Observable<UserData | null> {
        return this.http.patch<UserData | null>(`${this.BASE_URL}/${friendRequestId}`, update);
    }
}