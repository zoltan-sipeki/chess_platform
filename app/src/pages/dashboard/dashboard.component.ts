import { Component, inject, OnDestroy, OnInit, signal } from "@angular/core";
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
import { AlertComponent } from "../../components/alert/alert.component";

@Component({
    selector: "dashboard",
    templateUrl: "./dashboard.component.html",
    standalone: true,
    imports: [Navbar, PlayMenu, UserMenu, UserSearch, RouterOutlet, RouterLink, NotificationMenu, FriendList, AlertComponent, QueueToastComponent]
})
export class Dashboard implements OnInit, OnDestroy {

    private authService: AuthService = inject(AuthService);

    private bootstrapService: BootstrapService = inject(BootstrapService);

    logoutUrl = signal<string>("");

    ngOnInit(): void {
        this.logoutUrl.set(this.authService.createLogoutUrl());
        this.bootstrapService.run();
    }

    ngOnDestroy(): void {
    }
}