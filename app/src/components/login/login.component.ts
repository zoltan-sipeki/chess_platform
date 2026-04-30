import { Component, inject, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { AuthService } from "../../services/AuthService";

@Component({
    selector: "auth-redirect",
    template: ""
})
export class Login implements OnInit {

    private authService: AuthService = inject(AuthService);

    private router: Router = inject(Router);

    ngOnInit(): void {
        const redirectPath = sessionStorage.getItem("redirectPath");
        if (this.authService.isAuthenticated()) {
            if (redirectPath === "/") {
                this.router.navigate(["/dashboard"]);
            }
            else {
                const queryStartIndex = redirectPath!.indexOf("?");
                if (queryStartIndex === -1) {
                    this.router.navigate([redirectPath!]);
                }
                else {
                    const searchParams = new URLSearchParams(redirectPath!.substring(queryStartIndex));
                    this.router.navigate([redirectPath!.slice(0, queryStartIndex)], { queryParams: [...searchParams.entries()].reduce((acc, [key, value]) => ({ ...acc, [key]: value }), {}) });
                }
            }
        }
        else {
            if (redirectPath === "/") {
                this.router.navigate(["/"]);
            }
            else {
                this.authService.login();
            }
        }
    }
}   