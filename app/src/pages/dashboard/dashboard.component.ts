import { Component, inject, OnDestroy, OnInit } from "@angular/core";
import { RouterLink, RouterOutlet } from "@angular/router";
import { AlertComponent } from "../../components/alert/alert.component";
import { FriendList } from "../../components/friend-list/friend-list.component";
import { Navbar } from "../../components/navbar/navbar.component";
import { NotificationMenu } from "../../components/notification-menu/notification-menu.component";
import { PlayMenu } from "../../components/play-menu/play-menu.component";
import { QueueToastComponent } from "../../components/queue-toast/queue-toast.component";
import { UserMenu } from "../../components/user-menu/user-menu.component";
import { UserSearch } from "../../components/user-search/user-search.component";
import { BootstrapService } from "../../services/BootstrapService";

@Component({
    selector: "dashboard",
    templateUrl: "./dashboard.component.html",
    standalone: true,
    imports: [Navbar, PlayMenu, UserMenu, UserSearch, RouterOutlet, RouterLink, NotificationMenu, FriendList, AlertComponent, QueueToastComponent]
})
export class Dashboard implements OnInit, OnDestroy {

    private bootstrapService: BootstrapService = inject(BootstrapService);

    ngOnInit(): void {
        this.bootstrapService.run();
    }

    ngOnDestroy(): void {
    }
}