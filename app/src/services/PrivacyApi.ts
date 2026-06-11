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

    private http: HttpClient = inject(HttpClient);

    fetch(): Observable<PrivacySettings> {
        return this.http.get<PrivacySettings>("/api/privacy");
    }

    updateChatPrivacy(settings: PrivacySettings): Observable<PrivacySettings> {
        return this.http.patch<PrivacySettings>("/api/privacy/chat", settings);
    }

    updateMatchPrivacy(settings: PrivacySettings): Observable<PrivacySettings> {
        return this.http.patch<PrivacySettings>("/api/privacy/match", settings);
    }
}