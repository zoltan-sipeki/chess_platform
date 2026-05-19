import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { UserData } from "../types";

export interface FriendQuery {
    page?: number
    size?: number
    mutual?: boolean
}

export interface FriendList {
    total: number,
    friends: UserData[]
}

@Injectable({
    providedIn: 'root'
})
export class FriendService {

    private http: HttpClient = inject(HttpClient);

    fetchFriends(userId: string, query: FriendQuery): Observable<FriendList> {
        return this.http.get<FriendList>("/api/friends", { params: { userId, ...query } });
    }
}