import { Component, inject, input, OnDestroy, OnInit, signal } from "@angular/core";
import { RouterLink } from "@angular/router";
import { NgbDropdown, NgbDropdownItem, NgbDropdownMenu, NgbDropdownToggle } from "@ng-bootstrap/ng-bootstrap";
import { Subscription } from "rxjs";
import { AuthService } from "../../services/AuthService";
import { UserStore } from "../../services/UserStore";
import { UserData } from "../../types";
import { AvatarComponent } from "../avatar/avatar.component";

@Component({
    selector: "user-menu",
    templateUrl: "./user-menu.component.html",
    imports: [NgbDropdown, NgbDropdownToggle, NgbDropdownMenu, NgbDropdownItem, RouterLink, AvatarComponent],
})
export class UserMenu implements OnInit, OnDestroy {

    private userStore: UserStore = inject(UserStore);

    private userStoreSub?: Subscription;

    private authService: AuthService = inject(AuthService);

    clazz = input<string>("");

    logoutUrl = signal<string>("");

    userData = signal<UserData>({ id: "", displayName: "", avatar: "" });

    ngOnInit(): void {
        this.logoutUrl.set(this.authService.createLogoutUrl());
        this.userStoreSub = this.userStore.subscribe(user => {
            this.userData.set(user);
        });
    }

    ngOnDestroy(): void {
        this.userStoreSub?.unsubscribe();
    }
}