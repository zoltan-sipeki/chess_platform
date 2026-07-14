import { DatePipe, PercentPipe } from "@angular/common";
import { Component, inject, OnDestroy, OnInit, signal } from "@angular/core";
import { ActivatedRoute, RouterLink } from "@angular/router";
import { Subscription } from "rxjs";
import { MatchStat } from "../../api/MatchApi";
import { Notification as NotificationRelayEvent } from "../../api/NotificationApi";
import { RelationshipType } from "../../api/RelationshipApi";
import { EventService, FriendRequestAcceptedEvent, UnfriendEvent } from "../../services/EventService";
import { FriendRequestService } from "../../services/FriendRequestService";
import { FriendService } from "../../services/FriendService";
import { MatchmakingService } from "../../services/MatchmakingService";
import { ProfileService, UserProfile } from "../../services/ProfileService";
import { RelayService, UnfriendRelayEvent, UserUpdatedRelayEvent } from "../../services/RelayService";
import { AvatarComponent } from "../avatar/avatar.component";
import { MatchHistoryTable } from "../match-history-table/match-history-table.component";
import { User } from "../user/user.component";

export interface MatchStatsTotal {
    gamesPlayed: number;
    wins: number;
    losses: number;
    draws: number;
    winRatio: number;
}

@Component({
    selector: "user-profile-page",
    templateUrl: "user-profile-page.component.html",
    imports: [User, PercentPipe, DatePipe, RouterLink, MatchHistoryTable, AvatarComponent]
})
export class UserProfilePage implements OnInit, OnDestroy {

    private profileService: ProfileService = inject(ProfileService);

    private friendRequestService: FriendRequestService = inject(FriendRequestService);

    private friendService: FriendService = inject(FriendService);

    private queueService: MatchmakingService = inject(MatchmakingService);

    private eventService: EventService = inject(EventService);

    private relayService: RelayService = inject(RelayService);

    private routeSub?: Subscription;

    route: ActivatedRoute = inject(ActivatedRoute);

    profile = signal<UserProfile | null>(null);

    sendingRequest = signal<boolean>(false);

    requestSent = signal<boolean>(false);

    unfriending = signal<boolean>(false);

    ngOnInit(): void {
        this.routeSub = this.route.params.subscribe(params => {
            const userId = params["id"];
            if (userId == null) {
                return;
            }

            this.profileService.fetch(userId).subscribe(profile => {
                this.profile.set(profile);
            });
        });

        this.eventService.addEventListener("friend-request-accepted", this.onFriendRequestAccepted);
        this.eventService.addEventListener("unfriend", this.onUnfriend);

        this.relayService.subscribe("UNFRIEND", this.onRelayUnfriend);
        this.relayService.subscribe("NOTIFICATION", this.onRelayNotification);
        this.relayService.subscribe("USER_UPDATED", this.onRelayUserUpdated);
    }

    ngOnDestroy(): void {
        this.routeSub?.unsubscribe();
        this.eventService.removeEventListener("friend-request-accepted", this.onFriendRequestAccepted);
        this.eventService.removeEventListener("unfriend", this.onUnfriend);

        this.relayService.unsubscribe("UNFRIEND", this.onRelayUnfriend);
        this.relayService.unsubscribe("NOTIFICATION", this.onRelayNotification);
        this.relayService.unsubscribe("USER_UPDATED", this.onRelayUserUpdated);
    }


    private onRelayNotification = (e: NotificationRelayEvent): void => {
        if (e.type === "FRIEND_REQUEST_ACCEPTED" && e.sender.id === this.profile()?.user.id) {
            this.updateRelationship("FRIENDS");
            this.refreshFriends();
        }
    }

    private onRelayUnfriend = (e: UnfriendRelayEvent): void => {
        if (e.senderId === this.profile()?.user.id) {
            this.updateRelationship("NOT_RELATED");
            this.refreshFriends();
        }
    }

    private onRelayUserUpdated = (e: UserUpdatedRelayEvent): void => {
        if (e.id === this.profile()?.user.id) {
            const user = this.profile()?.user;
            if (user != null) {
                this.profile.update(p => {
                    if (p != null) {
                        return { ...p, user: e };
                    }
                    return p;
                });
            }
        }
    }

    calcMatchStatsTotal(matchStats: MatchStat[]): MatchStatsTotal {
        const result: MatchStatsTotal = {
            gamesPlayed: 0,
            wins: 0,
            losses: 0,
            draws: 0,
            winRatio: 0
        };

        for (const stat of matchStats) {
            result.gamesPlayed += stat.gamesPlayed;
            result.wins += stat.wins;
            result.losses += stat.losses;
            result.draws += stat.draws;
        }

        result.winRatio = result.gamesPlayed > 0 ? result.wins / result.gamesPlayed : 0;
        return result;
    }

    sendFriendRequest(): void {
        const profile = this.profile();
        if (profile != null) {
            this.sendingRequest.set(true);
            this.friendRequestService.sendRequest(profile.user.id).subscribe(friend => {
                this.sendingRequest.set(false);
                this.requestSent.set(true);
                if (friend != null) {
                    this.updateRelationship("FRIENDS");
                    this.refreshFriends();
                }
            });
        }
    }

    unfriend(): void {
        const profile = this.profile();
        if (profile != null) {
            this.unfriending.set(true);
            this.friendService.unfriend(profile.user.id).subscribe(() => {
                this.unfriending.set(false);
                this.refreshFriends();
            });
            this.updateRelationship("NOT_RELATED");
        }
    }

    invite(id: string): void {
        this.queueService.invite(id).subscribe();
    }

    private onFriendRequestAccepted = (e: FriendRequestAcceptedEvent): void => {
        if (e.details.friend.id === this.profile()?.user.id) {
            this.updateRelationship("FRIENDS");
            this.refreshFriends();
        }
    }

    private onUnfriend = (e: UnfriendEvent): void => {
        if (e.details.friend.id === this.profile()?.user.id) {
            this.updateRelationship("NOT_RELATED");
            this.refreshFriends();
        }
    }

    private updateRelationship(relationship: RelationshipType): void {
        this.profile.update(p => {
            if (p != null) {
                return { ...p, relationship };
            }

            return p;
        });
    }

    private refreshFriends(): void {
        const profile = this.profile();
        if (profile != null) {
            this.profileService.fetchFriends(profile.user.id).subscribe(friends => {
                this.profile.update(p => {
                    if (p != null) {
                        return { ...p, friends };
                    }
                    return p;
                });
            });
        }
    }

}