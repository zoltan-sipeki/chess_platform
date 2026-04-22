import { Component, inject, signal } from "@angular/core";
import { Navbar } from "../navbar/navbar.component";
import { AuthService } from "../../services/AuthService";

@Component({
    selector: "dashboard",
    templateUrl: "./dashboard.component.html",
    standalone: true,
    imports: [Navbar]
})
export class Dashboard {

    private authService: AuthService = inject(AuthService);

    public logoutUrl = signal<string>("");

    public ngOnInit(): void {
        this.logoutUrl.set(this.authService.createLogoutUrl());
    }
}