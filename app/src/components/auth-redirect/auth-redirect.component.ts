import { Component, inject } from "@angular/core";
import { Router } from "@angular/router";
import { AuthService } from "../../services/AuthService";

@Component({
    selector: "auth-redirect",
    template: ""
})
export class AuthRedirect {

    private authService: AuthService = inject(AuthService);

    private router: Router = inject(Router);

    public ngOnInit(): void {
        if (this.authService.isAuthenticated()) {
            this.router.navigate(["/dashboard"]);
        }
        else {
            this.router.navigate(["/home"]);
        }
    }
}   