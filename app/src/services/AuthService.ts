import { inject, Injectable } from "@angular/core";
import Keycloak from 'keycloak-js';
import { environment } from "../environments/environment";
import { ActivatedRoute, Router } from "@angular/router";
import { UserData } from "../types";

@Injectable({ providedIn: 'root' })
export class AuthService {

    private router: Router = inject(Router);

    private route: ActivatedRoute = inject(ActivatedRoute);

    private keycloak: Keycloak = new Keycloak({
        url: environment.KC_PROVIDER_URL,
        realm: environment.KC_REALM,
        clientId: environment.KC_CLIENT_ID
    });

    public init(): Promise<boolean> {

        return this.keycloak.init({
            pkceMethod: 'S256',
            redirectUri: `${window.location.origin}/login`,
            onLoad: "check-sso",
            silentCheckSsoRedirectUri: `${window.location.origin}/silent-check-sso.html`
        });

    }

    public login(): Promise<void> {
        return this.keycloak.login();
    }

    public async getAccessToken(): Promise<string | null> {
        try {
            const refreshed = await this.keycloak.updateToken(10);
            return this.keycloak.token ?? null;
        }
        catch (error) {
            return null;
        }
    }

    public createLoginUrl(): Promise<string> {
        return this.keycloak.createLoginUrl();
    }

    public createLogoutUrl(): string {
        return this.keycloak.createLogoutUrl({ redirectUri: window.location.origin });
    }

    public createRegisterUrl(): Promise<string> {
        return this.keycloak.createRegisterUrl();
    }

    public isAuthenticated(): boolean {
        return this.keycloak.authenticated;
    }

    public getUserData(): UserData {
        const id = this.keycloak.idTokenParsed!["sub"] ?? "";
        const displayName = this.keycloak.idTokenParsed!["displayName"];
        const avatar = this.keycloak.idTokenParsed!["avatar"];

        return { id, displayName, avatar };
    }
}