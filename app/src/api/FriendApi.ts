import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { UserData } from "./UserApi";

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
export class FriendApi {

    private http: HttpClient = inject(HttpClient);

    fetchAll(id: string, query: FriendQuery = {}): Observable<FriendList> {
        return this.http.get<FriendList>(`/api/users/${id}/friends`, { params: { ...query } });
    }

    fetchAllMe(query: FriendQuery = {}): Observable<FriendList> {
        return this.http.get<FriendList>("/api/users/me/friends", { params: { ...query } });
    }

    unfriend(id: string): Observable<void> {
        return this.http.delete<void>(`/api/users/me/friends/${id}`);
    }
}