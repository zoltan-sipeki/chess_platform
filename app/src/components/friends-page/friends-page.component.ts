import { AsyncPipe } from "@angular/common";
import { Component, inject, OnDestroy, OnInit, signal } from "@angular/core";
import { ActivatedRoute, RouterLink } from "@angular/router";
import { Observable, Subscription } from "rxjs";
import { FriendList, FriendService } from "../../services/FriendService";
import { Pagination } from "../pagination/pagination.component";
import { User } from "../user/user.component";

@Component({
    selector: 'friends-page',
    templateUrl: 'friends-page.component.html',
    imports: [User, AsyncPipe, RouterLink, Pagination]
})
export class FriendsPage implements OnInit, OnDestroy {

    private friendService: FriendService = inject(FriendService);

    private route: ActivatedRoute = inject(ActivatedRoute);

    private routeSub?: Subscription;

    friends$?: Observable<FriendList>;

    page = signal<number>(1);

    readonly PAGE_SIZE = 10;

    onPageChange(page: number): void {
        this.friends$ = this.friendService.fetchFriends(this.route.snapshot.params["id"], { page: this.page() - 1, size: this.PAGE_SIZE });
    }

    ngOnInit(): void {
        this.routeSub = this.route.params.subscribe(params => {
            const userId = params["id"];
            if (userId == null) {
                return;
            }

            this.friends$ = this.friendService.fetchFriends(userId, { size: 10 });
        });
    }

    ngOnDestroy(): void {
        this.routeSub?.unsubscribe();
    }
}