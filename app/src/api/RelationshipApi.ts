import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";

export type RelationshipType = "FRIENDS" | "SELF" | "NOT_RELATED";

export interface Relationship {
    relationship: RelationshipType
}

@Injectable({
    providedIn: 'root'
})
export class RelationshipApi {

    private http: HttpClient = inject(HttpClient);

    fetch(userId: string): Observable<Relationship> {
        return this.http.post<Relationship>("/api/relationships/search", { ids: [userId] });
    }

}