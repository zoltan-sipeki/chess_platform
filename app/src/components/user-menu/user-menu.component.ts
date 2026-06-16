import { Component, inject, input, OnDestroy, OnInit, Signal, signal } from '@angular/core';
import { RouterLink } from "@angular/router";
import { NgbDropdown, NgbDropdownItem, NgbDropdownMenu, NgbDropdownToggle } from "@ng-bootstrap/ng-bootstrap";
import { SnakeCaseToTitleCasePipe } from '../../pipes/SnakeCaseToTitleCase';
import { AuthService } from "../../services/AuthService";
import { PresenceService } from '../../services/PresenceService';
import { PresenceChangedRelayEvent, RelayService } from '../../services/RelayService';
import { Presence, UserData } from '../../services/UserApi';
import { UserService } from "../../services/UserService";
import { AvatarComponent } from "../avatar/avatar.component";
import { User } from '../user/user.component';

@Component({
    selector: "user-menu",
    templateUrl: "./user-menu.component.html",
    imports: [NgbDropdown, NgbDropdownToggle, NgbDropdownMenu, NgbDropdownItem, RouterLink, AvatarComponent, User, SnakeCaseToTitleCasePipe],
})
export class UserMenu implements OnInit, OnDestroy {

    private userService: UserService = inject(UserService);

    private authService: AuthService = inject(AuthService);

    private relayService: RelayService = inject(RelayService);

    private presenceService: PresenceService = inject(PresenceService);

    clazz = input<string>("");

    logoutUrl = signal<string>("");

    currentUser!: Signal<UserData>;

    presences = this.presenceService.PRESENCES;

    ngOnInit(): void {
        this.currentUser = this.userService.currentUser;
        this.logoutUrl.set(this.authService.createLogoutUrl());
        this.relayService.subscribe("PRESENCE_CHANGED", this.onRelayPresenceChanged);
    }

    ngOnDestroy(): void {
        this.relayService.unsubscribe("PRESENCE_CHANGED", this.onRelayPresenceChanged);
    }

    updatePreferredPresence(presence: Presence): void {
        this.userService.updatePreferredPresence(presence).subscribe();
    }

    private onRelayPresenceChanged = (e: PresenceChangedRelayEvent): void => {
        if (e.userId === this.currentUser().id) {
            this.userService.updatePresence(e.presence);
        }
    }
}