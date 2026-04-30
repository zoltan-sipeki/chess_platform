import { HttpEvent, HttpHandlerFn, HttpRequest } from "@angular/common/http";
import { inject } from "@angular/core";
import { Observable } from "rxjs";
import { AuthService } from "../services/AuthService";

export function authInterceptor(req: HttpRequest<unknown>, next: HttpHandlerFn): Observable<HttpEvent<unknown>> {
    const authService = inject(AuthService);
    return new Observable<HttpEvent<unknown>>(subscriber => {
        authService.getAccessToken().then(accesToken => {
            if (accesToken != null) {
                const authReq = req.clone({ headers: req.headers.append("Authorization", `Bearer ${accesToken}`) });
                next(authReq).subscribe(subscriber);
            }
            else {
                subscriber.error("Failed to obtain access token.");
            }
        });
    });
}