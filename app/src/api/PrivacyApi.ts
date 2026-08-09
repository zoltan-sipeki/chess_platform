import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";

export type PrivacySetting = "PUBLIC" | "PRIVATE" | "FRIENDS";


export interface PrivacySettings {
    friends?: PrivacySetting,
    playerStats?: PrivacySetting,
    matchStats?: PrivacySetting,
    matchHistory?: PrivacySetting
}


@Injectable({ providedIn: 'root' })
export class PrivacyApi {

    private BASE_URL = "/api/privacy";

    private http: HttpClient = inject(HttpClient);

    fetch(): Observable<PrivacySettings> {
        return this.http.get<PrivacySettings>(`${this.BASE_URL}`);
    }

    updateChatPrivacy(settings: PrivacySettings): Observable<PrivacySettings> {
        return this.http.patch<PrivacySettings>(`${this.BASE_URL}}/chat`, settings);
    }

    updateMatchPrivacy(settings: PrivacySettings): Observable<PrivacySettings> {
        return this.http.patch<PrivacySettings>(`${this.BASE_URL}/match`, settings);
    }
}