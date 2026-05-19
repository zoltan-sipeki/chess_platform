import { AsyncPipe, DatePipe, PercentPipe } from "@angular/common";
import { Component, inject, OnDestroy, OnInit } from "@angular/core";
import { ActivatedRoute, RouterLink } from "@angular/router";
import { Observable, Subscription } from "rxjs";
import { MatchStat } from "../../services/MatchService";
import { UserProfile, UserService } from "../../services/UserService";
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
    imports: [User, PercentPipe, DatePipe, RouterLink, MatchHistoryTable, AsyncPipe]
})
export class UserProfilePage implements OnInit, OnDestroy {

    private userService: UserService = inject(UserService);

    private routeSub?: Subscription;

    route: ActivatedRoute = inject(ActivatedRoute);

    profile$?: Observable<UserProfile>;

    ngOnInit(): void {
        this.routeSub = this.route.params.subscribe(params => {
            const userId = params["id"];
            if (userId == null) {
                return;
            }

            this.profile$ = this.userService.fetchProfile(userId);
        });
    }

    ngOnDestroy(): void {
        this.routeSub?.unsubscribe();
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

}