import { Component, inject, OnInit, signal } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { UserSearchResult, UserService } from "../../services/UserService";
import { Pagination } from "../pagination/pagination.component";
import { User } from "../user/user.component";

@Component({
    selector: "user-search-page",
    templateUrl: "user-search-page.component.html",
    imports: [User, Pagination]
})
export class UserSearchPage implements OnInit {

    readonly PAGE_SIZE: number = 8;

    private route: ActivatedRoute = inject(ActivatedRoute);

    private userService: UserService = inject(UserService);

    searchResult = signal<UserSearchResult | null>(null);

    prefix = signal<string>("");

    page = signal<number>(1);

    loading = signal<boolean>(true);

    ngOnInit(): void {
        this.route.queryParams.subscribe(async params => {
            const prefix = params["startsWith"];

            this.prefix.set(prefix);
            this.userService.fetchUsersByDisplayNamePrefix(prefix, { page: this.page() - 1, size: this.PAGE_SIZE }).subscribe(result => {
                this.searchResult.set(result);
                this.loading.set(false);
            });
        });
    }

    onPageChange(page: number): void {
        this.loading.set(true);
        this.userService.fetchUsersByDisplayNamePrefix(this.prefix(), { page: page - 1, size: this.PAGE_SIZE }).subscribe(result => {
            this.searchResult.set(result);
            this.loading.set(false);
        });
    }
}