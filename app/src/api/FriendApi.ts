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

    private BASE_URL = '/api/users';

    private http: HttpClient = inject(HttpClient);

    fetchAll(id: string, query: FriendQuery = {}): Observable<FriendList> {
        return this.http.get<FriendList>(`${this.BASE_URL}/${id}/friends`, { params: { ...query } });
    }

    fetchAllMe(query: FriendQuery = {}): Observable<FriendList> {
        return this.http.get<FriendList>( `${this.BASE_URL}/me/friends`, { params: { ...query } });
    }

    unfriend(id: string): Observable<void> {
        return this.http.delete<void>(`${this.BASE_URL}/me/friends/${id}`);
    }
}