import { AsyncPipe } from "@angular/common";
import { Component, inject, OnDestroy, OnInit, signal } from "@angular/core";
import { FormControl, FormGroup, ReactiveFormsModule } from "@angular/forms";
import { ActivatedRoute } from "@angular/router";
import { Observable, shareReplay, Subscription } from "rxjs";
import { MatchApi, MatchHistoryList } from "../../api/MatchApi";
import { MatchHistoryTable } from "../../components/match-history-table/match-history-table.component";
import { Pagination } from "../../components/pagination/pagination.component";

@Component({
    selector: "match-history-page",
    templateUrl: "match-history-page.component.html",
    imports: [MatchHistoryTable, AsyncPipe, ReactiveFormsModule, Pagination]
})
export class MatchHistoryPage implements OnInit, OnDestroy {

    private matchApi: MatchApi = inject(MatchApi);

    private route: ActivatedRoute = inject(ActivatedRoute);

    private routeSub?: Subscription

    matches$?: Observable<MatchHistoryList>;

    page = signal<number>(1);

    readonly PAGE_SIZE = 8;

    form: FormGroup = new FormGroup({
        matchType: new FormControl("", { nonNullable: true }),
        outcome: new FormControl("", { nonNullable: true }),
        dateSort: new FormControl("", { nonNullable: true })
    });

    onSubmit(): void {
        const matchType = this.form.get("matchType")?.value;
        const outcome = this.form.get("outcome")?.value;
        const sort = this.form.get("dateSort")?.value;

        this.matches$ = this.matchApi.fetchMatchHistory(this.route.snapshot.params["id"], { matchType, outcome, page: this.page() - 1, sort, size: this.PAGE_SIZE }).pipe(shareReplay(1));
        this.matches$.subscribe(r => console.log(r));
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