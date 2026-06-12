import { SlicePipe } from "@angular/common";
import { Component, inject, input, OnDestroy, OnInit, Signal } from "@angular/core";
import { NgbDropdown, NgbDropdownItem, NgbDropdownMenu, NgbDropdownToggle } from "@ng-bootstrap/ng-bootstrap";
import { TimeAgoPipe } from "../../pipes/TimeAgoPipe";
import { FriendRequestService } from "../../services/FriendRequestService";
import { Notification, NotificationList } from "../../services/NotificationApi";
import { NotificationService } from "../../services/NotificationService";
import { AvatarComponent } from "../avatar/avatar.component";

@Component({
    selector: "notification-list",
    templateUrl: "notification-list.component.html",
    styleUrl: "notification-list.component.css",
    standalone: true,
    imports: [TimeAgoPipe, AvatarComponent, NgbDropdown, NgbDropdownItem, NgbDropdownMenu, NgbDropdownToggle, SlicePipe]
})
export class NotificationListComponent implements OnInit, OnDestroy {

    private friendRequestService: FriendRequestService = inject(FriendRequestService);

    private notificationService: NotificationService = inject(NotificationService);

    maxSize = input<number>();

    list!: Signal<NotificationList>;

    ngOnChanges(): void {

    }

    ngOnInit(): void {
        this.list = this.notificationService.notifications;
    }

    ngOnDestroy(): void {
        if (this.maxSize() == null) {
            this.notificationService.trim();
        }
    }

    acceptFriendRequest(notification: Notification): void {
        if (notification.friendRequest != null) {
            this.notificationService.setAccepting(notification.id, true);
            this.friendRequestService.acceptRequest(notification.friendRequest).subscribe();
        }
    }

    rejectFriendRequest(notification: Notification): void {
        if (notification.friendRequest != null) {
            this.notificationService.setRejecting(notification.id, true);
            this.friendRequestService.rejectRequest(notification.friendRequest).subscribe();
        }
    }

    deleteNotification(notification: Notification): void {
        this.notificationService.delete(notification.id).subscribe();
    }

    loadMore(): void {
        this.notificationService.loadMore(this.maxSize()).subscribe();
    }

    isRequestPending(id: string): boolean {
        return !!this.notificationService.getAccepting(id)?.() || !!this.notificationService.getRejecting(id)?.();
    }

    isRequestAccepting(id: string): boolean {
        return !!this.notificationService.getAccepting(id)?.();
    }

    isRequestRejecting(id: string): boolean {
        return !!this.notificationService.getRejecting(id)?.();
    }
}