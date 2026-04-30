import { Component, inject, input, OnInit, signal } from "@angular/core";
import { NgbDropdown, NgbDropdownItem, NgbDropdownMenu, NgbDropdownToggle } from "@ng-bootstrap/ng-bootstrap";
import { AuthService } from "../../services/AuthService";
import { UserData } from "../../types";
import { RouterLink } from "@angular/router";

@Component({
    selector: "user-menu",
    templateUrl: "./user-menu.component.html",
    imports: [NgbDropdown, NgbDropdownToggle, NgbDropdownMenu, NgbDropdownItem, RouterLink]
})
export class UserMenu implements OnInit {

    private authService: AuthService = inject(AuthService);

    clazz = input<string>("");

    logoutUrl = signal<string>("");

    userData = signal<UserData>({ id: "", displayName: "", avatar: "" });

    ngOnInit(): void {
        this.logoutUrl.set(this.authService.createLogoutUrl());
        this.userData.set(this.authService.getUserData());
    }
}