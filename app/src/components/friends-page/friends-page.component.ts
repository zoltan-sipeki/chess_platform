import { AsyncPipe } from "@angular/common";
import { Component, inject, OnDestroy, OnInit, signal } from "@angular/core";
import { ActivatedRoute, RouterLink } from "@angular/router";
import { Observable, Subscription } from "rxjs";
import { FriendApi, FriendList } from "../../services/FriendApi";
import { Pagination } from "../pagination/pagination.component";
import { User } from "../user/user.component";

@Component({
    selector: 'friends-page',
    templateUrl: 'friends-page.component.html',
    imports: [User, AsyncPipe, RouterLink, Pagination]
})
export class FriendsPage implements OnInit, OnDestroy {

    private friendApi: FriendApi = inject(FriendApi);

    private route: ActivatedRoute = inject(ActivatedRoute);

    private routeSub?: Subscription;

    friends$?: Observable<FriendList>;

    page = signal<number>(1);

    readonly PAGE_SIZE = 10;

    onPageChange(page: number): void {
        this.friends$ = this.friendApi.fetchAll({ userId: this.route.snapshot.params["id"], page: this.page() - 1, size: this.PAGE_SIZE });
    }

    ngOnInit(): void {
        this.routeSub = this.route.params.subscribe(params => {
            const userId = params["id"];
            if (userId == null) {
                return;
            }

            this.friends$ = this.friendApi.fetchAll({ userId, size: 10 });
        });
    }

    ngOnDestroy(): void {
        this.routeSub?.unsubscribe();
    }
}