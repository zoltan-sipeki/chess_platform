import { inject } from "@angular/core";
import { Route, UrlSegment } from "@angular/router";
import { AuthService } from "../services/AuthService";

export function authMatch(route: Route, segments: UrlSegment[]): boolean {
    const authService = inject(AuthService);
    return authService.isAuthenticated();
}