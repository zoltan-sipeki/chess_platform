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

        if (redirectPath === null) {
            return;
        }

        if (redirectPath === "/") {
            this.router.navigate(["/"]);
            return;
        }

        if (this.authService.isAuthenticated()) {
            const urlTree = this.router.parseUrl(redirectPath!);
            this.router.navigateByUrl(urlTree);
        }
        else {
            this.authService.login();
        }
    }
}   