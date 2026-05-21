import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";

export interface Avatar {
    id: string;
}

@Injectable({
    providedIn: "root",
})
export class AvatarService {

    private http: HttpClient = inject(HttpClient);

    upload(avatar: Blob): Observable<Avatar> {
        const formData = new FormData();
        formData.append("file", avatar);
        return this.http.post<Avatar>("/api/avatars", formData);
    }

    delete(): Observable<Avatar> {
        return this.http.delete<Avatar>("/api/avatars/me");
    }
}