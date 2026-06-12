import { inject, Injectable } from "@angular/core";
import { forkJoin } from "rxjs";
import { FriendRequestService } from "./FriendRequestService";
import { NotificationService } from "./NotificationService";
import { UserService } from "./UserService";
import { FriendService } from "./FriendService";

@Injectable({
    providedIn: "root",
})
export class BootstrapService {

    private notificationService: NotificationService = inject(NotificationService);

    private friendRequestService: FriendRequestService = inject(FriendRequestService);

    private userService: UserService = inject(UserService);

    private friendService: FriendService = inject(FriendService);

    run(): void {
        forkJoin([
            this.userService.refresh(),
            this.friendService.refresh(),
            this.notificationService.refresh(),
            this.friendRequestService.refresh()]).subscribe();
    }
}