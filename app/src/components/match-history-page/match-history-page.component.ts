import { AsyncPipe } from "@angular/common";
import { Component, inject, OnDestroy, OnInit, signal } from "@angular/core";
import { FormControl, FormGroup, ReactiveFormsModule } from "@angular/forms";
import { ActivatedRoute } from "@angular/router";
import { Observable, Subscription } from "rxjs";
import { MatchHistoryList, MatchService } from "../../services/MatchService";
import { MatchHistoryTable } from "../match-history-table/match-history-table.component";
import { Pagination } from "../pagination/pagination.component";

@Component({
    selector: "match-history-page",
    templateUrl: "match-history-page.component.html",
    imports: [MatchHistoryTable, AsyncPipe, ReactiveFormsModule, Pagination]
})
export class MatchHistoryPage implements OnInit, OnDestroy {

    private matchService: MatchService = inject(MatchService);

    private route: ActivatedRoute = inject(ActivatedRoute);

    private routeSub?: Subscription

    matches$?: Observable<MatchHistoryList>;

    page = signal<number>(1);

    readonly PAGE_SIZE = 8;

    form: FormGroup = new FormGroup({
        matchType: new FormControl("", { nonNullable: true }),
        outcome: new FormControl("", { nonNullable: true }),
        dateSort: new FormControl("match.startedAt,desc", { nonNullable: true })
    });

    onSubmit(): void {
        const matchType = this.form.get("matchType")?.value;
        const outcome = this.form.get("outcome")?.value;
        const sort = this.form.get("dateSort")?.value;

        this.matches$ = this.matchService.fetchMatchHistory(this.route.snapshot.params["id"], { matchType, outcome, page: this.page() - 1, sort, size: 10 });
    }

    clearFilters(): void {
        this.form.reset();
        this.onSubmit();
    }

    onPageChange(page: number): void {
        this.onSubmit();
    }

    ngOnInit(): void {
        this.routeSub = this.route.params.subscribe(params => {
            const userId = params["id"];
            if (userId == null) {
                return;
            }

            this.onSubmit();
        });
    }

    ngOnDestroy(): void {
        this.routeSub?.unsubscribe();
    }

}