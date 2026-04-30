import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { UserSearchResult } from "../types";

@Injectable({ providedIn: 'root' })
export class UserService {

    private http: HttpClient = inject(HttpClient);

    constructor() { }

    public findUsersByDisplayNamePrefix(prefix: string, limit: number = 5): Promise<UserSearchResult> {
        if (prefix.length === 0) {
            return Promise.resolve({ hasMore: false, users: [] });
        }

        return new Promise<UserSearchResult>((resolve, reject) => {
            this.http.get<UserSearchResult>("/api/users", {
                params: {
                    startsWith: prefix,
                    limit
                }
            }).subscribe({
                next: result => resolve(result),
                error: err => reject(err)
            });
        });
    }
}