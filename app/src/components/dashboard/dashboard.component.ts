import { Component, inject, OnDestroy, OnInit, signal, viewChild } from "@angular/core";
import { RouterLink, RouterOutlet } from "@angular/router";
import { NgbAlert } from "@ng-bootstrap/ng-bootstrap";
import { AuthService } from "../../services/AuthService";
import { DashboardService } from "../../services/DashboardService";
import { AlertEvent, AlertEventDetails, EventService } from "../../services/EventService";
import { FriendList } from "../friend-list/friend-list.component";
import { Navbar } from "../navbar/navbar.component";
import { NotificationMenu } from "../notification-menu/notification-menu.component";
import { PlayMenu } from "../play-menu/play-menu.component";
import { UserMenu } from "../user-menu/user-menu.component";
import { UserSearch } from "../user-search/user-search.component";

@Component({
    selector: "dashboard",
    templateUrl: "./dashboard.component.html",
    imports: [Navbar, PlayMenu, UserMenu, UserSearch, RouterOutlet, RouterLink, NotificationMenu, FriendList, NgbAlert]
})
export class Dashboard implements OnInit, OnDestroy {

    private authService: AuthService = inject(AuthService);

    private dashboardService: DashboardService = inject(DashboardService);

    private eventService: EventService = inject(EventService);

    private alert = viewChild<NgbAlert>("alert");

    private timeout: number = -1;

    alertDetails = signal<AlertEventDetails | null>(null);

    logoutUrl = signal<string>("");

    ngOnInit(): void {
        this.eventService.addEventListener("alert", this.onAlert);
        this.logoutUrl.set(this.authService.createLogoutUrl());
        this.dashboardService.fetch();
    }

    ngOnDestroy(): void {
        this.eventService.removeEventListener("alert", this.onAlert);
        clearTimeout(this.timeout);
    }

    onAlert = (e: AlertEvent): void => {
        this.alertDetails.set(e.details);
        this.timeout = setTimeout(() => this.alert()?.close(), 5000);
    }

    onAlertClose(): void {
        this.alertDetails.set(null);
    }
}