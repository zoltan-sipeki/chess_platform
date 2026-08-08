import { inject, Injectable } from "@angular/core";
import { ChatService } from "./ChatService";
import { FriendRequestService } from "./FriendRequestService";
import { FriendService } from "./FriendService";
import { MatchmakingService } from "./MatchmakingService";
import { NotificationService } from "./NotificationService";
import { UserService } from "./UserService";

@Injectable({
    providedIn: "root",
})
export class BootstrapService {

    private notificationService: NotificationService = inject(NotificationService);

    private friendRequestService: FriendRequestService = inject(FriendRequestService);

    private userService: UserService = inject(UserService);

    private friendService: FriendService = inject(FriendService);

    private matchmakingService: MatchmakingService = inject(MatchmakingService);

    private chatService: ChatService = inject(ChatService);

    run(): void {
        this.userService.refresh().subscribe();
        this.friendService.refresh().subscribe();
        this.chatService.fetchChannels().subscribe();
        this.notificationService.refresh().subscribe();
        this.friendRequestService.refresh().subscribe();
        this.matchmakingService.fetchCurrentMatch().subscribe();
    }
}