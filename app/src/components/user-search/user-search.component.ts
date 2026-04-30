import { Component, inject, OnDestroy, OnInit, signal, viewChild } from "@angular/core";
import { FormControl, ReactiveFormsModule } from "@angular/forms";
import { Router, RouterLink } from "@angular/router";
import { NgbDropdown, NgbDropdownItem, NgbDropdownMenu } from "@ng-bootstrap/ng-bootstrap";
import { DebounceService } from "../../services/DebounceService";
import { UserService } from "../../services/UserService";
import { UserSearchResult } from "../../types";
import { User } from "../user/user.component";

@Component({
    selector: "user-search",
    templateUrl: "user-search.component.html",
    imports: [NgbDropdown, NgbDropdownMenu, NgbDropdownItem, User, ReactiveFormsModule, RouterLink],
    providers: [DebounceService]
})
export class UserSearch implements OnInit, OnDestroy {

    private debounceService: DebounceService = inject(DebounceService);

    private router: Router = inject(Router);

    private userService: UserService = inject(UserService);

    private dropdown = viewChild(NgbDropdown);

    userSearchResult = signal<UserSearchResult>({ hasMore: false, users: [] });

    loading = signal<boolean>(false);

    prefix = new FormControl<string>("");

    ngOnInit(): void {
        this.prefix.valueChanges.subscribe(prefix => {
            if (prefix == null || prefix.length === 0) {
                this.debounceService.cancel();
                this.userSearchResult.set({ hasMore: false, users: [] });
                this.loading.set(false);
                this.dropdown()?.close();
                return;
            }

            this.loading.set(true);

            this.debounceService.debounce(async () => {
                const result = await this.userService.findUsersByDisplayNamePrefix(prefix);
                this.userSearchResult.set(result);
                if (result.users.length > 0) {
                    this.dropdown()?.open();
                }
                this.loading.set(false);
            }, 200);
        })
    }

    ngOnDestroy(): void {
        this.debounceService.cancel();
    }

    openList(): void {
        if (this.userSearchResult().users.length > 0) {
            this.dropdown()?.open();
        }
    }

    redirect(e: Event): void {
        e.preventDefault();
        if (this.prefix.value != null && this.prefix.value.length > 0) {
            this.router.navigate(["/dashboard/users"], { queryParams: { startsWith: this.prefix.value } });
        }
        this.prefix.setValue("");
        this.dropdown()?.close();
    }
}