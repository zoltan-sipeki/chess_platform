import { Component, inject, OnInit, signal } from "@angular/core";
import { AuthService } from "../../services/AuthService";
import { Navbar } from "../navbar/navbar.component";
import { PlayMenu } from "../play-menu/play-menu.component";
import { UserMenu } from "../user-menu/user-menu.component";
import { UserSearch } from "../user-search/user-search.component";
import { RouterLink, RouterOutlet } from "@angular/router";
import { NotificationMenu } from "../notification-menu/notification-menu.component";

@Component({
    selector: "dashboard",
    templateUrl: "./dashboard.component.html",
    imports: [Navbar, PlayMenu, UserMenu, UserSearch, RouterOutlet, RouterLink, NotificationMenu]
})
export class Dashboard implements OnInit {

    private authService: AuthService = inject(AuthService);

    logoutUrl = signal<string>("");

    async ngOnInit(): Promise<void> {
        this.logoutUrl.set(this.authService.createLogoutUrl());
    }
}