import { Component, inject, OnDestroy, OnInit, signal, viewChild } from "@angular/core";
import { RouterLink, RouterOutlet } from "@angular/router";
import { NgbAlert } from "@ng-bootstrap/ng-bootstrap";
import { FriendList } from "../../components/friend-list/friend-list.component";
import { Navbar } from "../../components/navbar/navbar.component";
import { NotificationMenu } from "../../components/notification-menu/notification-menu.component";
import { PlayMenu } from "../../components/play-menu/play-menu.component";
import { QueueToastComponent } from "../../components/queue-toast/queue-toast.component";
import { UserMenu } from "../../components/user-menu/user-menu.component";
import { UserSearch } from "../../components/user-search/user-search.component";
import { AuthService } from "../../services/AuthService";
import { BootstrapService } from "../../services/BootstrapService";
import { AlertEvent, AlertType, EventService } from "../../services/EventService";

@Component({
    selector: "dashboard",
    templateUrl: "./dashboard.component.html",
    standalone: true,
    imports: [Navbar, PlayMenu, UserMenu, UserSearch, RouterOutlet, RouterLink, NotificationMenu, FriendList, NgbAlert, QueueToastComponent]
})
export class Dashboard implements OnInit, OnDestroy {

    private authService: AuthService = inject(AuthService);

    private bootstrapService: BootstrapService = inject(BootstrapService);

    private eventService: EventService = inject(EventService);

    private alert = viewChild<NgbAlert>("alert");

    private timeout: number = -1;

    alertDetails = signal<{ type: AlertType, message: string } | null>(null);

    logoutUrl = signal<string>("");

    ngOnInit(): void {
        this.eventService.addEventListener("alert", this.onAlert);
        this.logoutUrl.set(this.authService.createLogoutUrl());
        this.bootstrapService.run();
    }

    ngOnDestroy(): void {
        this.eventService.removeEventListener("alert", this.onAlert);
        clearTimeout(this.timeout);
    }

    onAlert = (e: AlertEvent): void => {
        this.alertDetails.set(e.details);
        clearTimeout(this.timeout);
        this.timeout = setTimeout(() => this.alert()?.close(), 5000);
    }

    onAlertClose(): void {
        this.alertDetails.set(null);
    }
}