import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root',
})
export class MessageApi {

    private readonly BASE_URL = '/api/messages';

    private http: HttpClient = inject(HttpClient);

    updateContent(id: string, body: any): Observable<void> {
        return this.http.patch<void>(
            `${this.BASE_URL}/${id}`,
            body
        );
    }

    deleteMessage(id: string): Observable<void> {
        return this.http.delete<void>(`${this.BASE_URL}/${id}`);
    }
}