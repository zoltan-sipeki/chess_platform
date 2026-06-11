import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";

export type Relationship = "FRIENDS" | "SELF" | "NOT_RELATED";

export type PrivacySetting = "PUBLIC" | "PRIVATE" | "FRIENDS";

export interface UserSearchResult {
    total: number,
    users: UserData[]
}

export interface UserData {
    id: string;
    displayName: string;
    avatar: string;
}

export interface UserSearchQuery {
    page?: number,
    size?: number
};

@Injectable({ providedIn: 'root' })
export class UserApi {

    private http: HttpClient = inject(HttpClient);

    fetchByDisplayNamePrefix(prefix: string, query: UserSearchQuery): Observable<UserSearchResult> {
        return this.http.get<UserSearchResult>("/api/users", {
            params: {
                startsWith: prefix,
                ...query
            }
        });

    }

    fetch(id: string): Observable<UserData> {
        return this.http.get<UserData>(`/api/users/${id}`);
    }

    fetchMe(): Observable<UserData> {
        return this.http.get<UserData>("/api/users/me");
    }

    updateDisplayName(displayName: string): Observable<UserData> {
        return this.http.patch<UserData>("/api/users/me", { displayName });
    }
}