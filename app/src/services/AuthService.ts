import { Injectable } from "@angular/core";
import { environment } from "../environments/environment";
import Keycloak from 'keycloak-js';

@Injectable({ providedIn: 'root' })
export class AuthService {

    private keycloak: Keycloak = new Keycloak({
        url: environment.KC_PROVIDER_URL,
        realm: environment.KC_REALM,
        clientId: environment.KC_CLIENT_ID
    });

    public init(): Promise<boolean> {
        return this.keycloak.init({
            pkceMethod: 'S256',
            redirectUri: environment.KC_CALLBACK_URL,
            onLoad: "check-sso",
            silentCheckSsoRedirectUri: `${environment.KC_CALLBACK_URL}/silent-check-sso.html`
        });
    }

    public createLoginUrl(): Promise<string> {
        return this.keycloak.createLoginUrl();
    }

    public createRegisterUrl(): Promise<string> {
        return this.keycloak.createRegisterUrl();
    }

    public isAuthenticated(): boolean {
        return this.keycloak.authenticated;
    }
}