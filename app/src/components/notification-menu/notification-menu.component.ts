import { Component, inject, OnDestroy, OnInit, Signal } from "@angular/core";
import { RouterLink } from "@angular/router";
import { NgbDropdown, NgbDropdownMenu, NgbDropdownToggle } from "@ng-bootstrap/ng-bootstrap";
import { NotificationList } from "../../services/NotificationApi";
import { NotificationService } from "../../services/NotificationService";
import { NotificationListComponent } from "../notification-list/notification-list.component";

@Component({
    selector: "notification-menu",
    templateUrl: "notification-menu.component.html",
    styleUrl: "notification-menu.component.css",
    imports: [NgbDropdown, NgbDropdownToggle, NgbDropdownMenu, RouterLink, NotificationListComponent]
})
export class NotificationMenu implements OnInit, OnDestroy {

    private notificationService: NotificationService = inject(NotificationService);

    list!: Signal<NotificationList>;

    ngOnInit(): void {
        this.list = this.notificationService.notifications;
    }

    ngOnDestroy(): void {
    }

    onOpen(open: boolean): void {
        if (open) {
            const list = this.list();
            if (list.unread > 0 && list.notifications.length > 0) {
                this.notificationService.updateAll({ lastReadSequenceNumber: list.notifications[0].seq }).subscribe();
            }

            this.notificationService.updateUnreadCount(0);
        }
    }

}