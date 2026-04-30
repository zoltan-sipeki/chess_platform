import { SlicePipe } from "@angular/common";
import { Component, inject, OnInit, signal } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { UserService } from "../../services/UserService";
import { UserSearchResult } from "../../types";
import { Pagination } from "../pagination/pagination.component";
import { User } from "../user/user.component";

@Component({
    selector: "user-search-page",
    templateUrl: "user-search-page.component.html",
    imports: [User, SlicePipe, Pagination]
})
export class UserSearchPage implements OnInit {

    private route: ActivatedRoute = inject(ActivatedRoute);

    private userService: UserService = inject(UserService);

    userSearchResult = signal<UserSearchResult>({ hasMore: false, users: [] });

    prefix = signal<string>("");

    page = signal<number>(1);

    pageSize = 10;

    ngOnInit(): void {
        this.route.queryParams.subscribe(async params => {
            const prefix = params["startsWith"];

            this.prefix.set(prefix);
            const result = await this.userService.findUsersByDisplayNamePrefix(prefix, 100);
            this.userSearchResult.set(result);
        });
    }
}