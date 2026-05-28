import { AsyncPipe, PercentPipe, SlicePipe } from "@angular/common";
import { Component, inject, OnInit, signal } from "@angular/core";
import { RouterLink } from "@angular/router";
import { Observable } from "rxjs";
import { LeaderboardEntry, MatchService } from "../../services/MatchService";
import { Pagination } from "../pagination/pagination.component";
import { User } from "../user/user.component";
import { MedalPipe } from "./MedalPipe";

@Component({
    selector: "leaderboard",
    templateUrl: "leaderboard.component.html",
    styleUrl: "leaderboard.component.css",
    imports: [User, PercentPipe, Pagination, SlicePipe, RouterLink, MedalPipe, AsyncPipe]
})
export class Leaderboard implements OnInit {

    private matchService: MatchService = inject(MatchService);

    leaderboard$?: Observable<LeaderboardEntry[]>;

    page = signal<number>(1);

    pageSize = 8;

    public ngOnInit(): void {
        this.leaderboard$ = this.matchService.fetchLeaderboard();
    }

}