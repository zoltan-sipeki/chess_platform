import { Component, inject, input, OnDestroy, OnInit, signal, Signal } from '@angular/core';
import { RouterLink } from "@angular/router";
import { NgbDropdown, NgbDropdownItem, NgbDropdownMenu, NgbDropdownToggle } from "@ng-bootstrap/ng-bootstrap";
import { Subscription } from "rxjs";
import { AuthService } from "../../services/AuthService";
import { UserStore } from "../../services/UserStore";
import { UserData } from "../../types";
import { AvatarComponent } from "../avatar/avatar.component";
import { UserService } from "../../services/UserService";

@Component({
    selector: "user-menu",
    templateUrl: "./user-menu.component.html",
    imports: [NgbDropdown, NgbDropdownToggle, NgbDropdownMenu, NgbDropdownItem, RouterLink, AvatarComponent],
})
export class UserMenu implements OnInit, OnDestroy {

    private userService: UserService = inject(UserService);

    private authService: AuthService = inject(AuthService);

    clazz = input<string>("");

    logoutUrl = signal<string>("");

    currentUser!: Signal<UserData>;

    ngOnInit(): void {
        this.currentUser = this.userService.currentUser;
        this.logoutUrl.set(this.authService.createLogoutUrl());
    }

    ngOnDestroy(): void {
    }
}