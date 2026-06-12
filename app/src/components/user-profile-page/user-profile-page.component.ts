import { DatePipe, PercentPipe } from "@angular/common";
import { Component, inject, OnDestroy, OnInit, signal } from "@angular/core";
import { ActivatedRoute, RouterLink } from "@angular/router";
import { Subscription } from "rxjs";
import { EventService, FriendRequestAcceptedEvent, UnfriendEvent } from "../../services/EventService";
import { FriendRequestApi } from "../../services/FriendRequestApi";
import { FriendService } from "../../services/FriendService";
import { MatchStat } from "../../services/MatchApi";
import { ProfileService, UserProfile } from "../../services/ProfileService";
import { RelationshipType } from "../../services/RelationshipApi";
import { AvatarComponent } from "../avatar/avatar.component";
import { MatchHistoryTable } from "../match-history-table/match-history-table.component";
import { User } from "../user/user.component";
import { FriendRequestService } from "../../services/FriendRequestService";

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

    private eventService: EventService = inject(EventService);

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
    }

    ngOnDestroy(): void {
        this.routeSub?.unsubscribe();
        this.eventService.removeEventListener("friend-request-accepted", this.onFriendRequestAccepted);
        this.eventService.removeEventListener("unfriend", this.onUnfriend);
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