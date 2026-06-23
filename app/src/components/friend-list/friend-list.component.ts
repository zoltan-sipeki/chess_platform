import { Component, inject, OnDestroy, OnInit, Signal } from "@angular/core";
import { FormControl, ReactiveFormsModule } from "@angular/forms";
import { RouterLink } from "@angular/router";
import { NgbAccordionBody, NgbAccordionButton, NgbAccordionCollapse, NgbAccordionDirective, NgbAccordionHeader, NgbAccordionItem, NgbAccordionToggle, NgbDropdown, NgbDropdownButtonItem, NgbDropdownItem, NgbDropdownMenu, NgbDropdownToggle, NgbNav, NgbNavContent, NgbNavItem, NgbNavItemRole, NgbNavLinkBase, NgbNavLinkButton, NgbNavOutlet } from "@ng-bootstrap/ng-bootstrap";
import { FriendRequest } from "../../services/FriendRequestApi";
import { FriendRequestService } from "../../services/FriendRequestService";
import { FriendService } from "../../services/FriendService";
import { QueueService } from "../../services/QueueService";
import { UserData } from "../../services/UserApi";
import { User } from "../user/user.component";

@Component({
    selector: 'friend-list',
    templateUrl: 'friend-list.component.html',
    styleUrl: 'friend-list.component.css',
    imports: [
        NgbAccordionButton,
        NgbAccordionDirective,
        NgbAccordionItem,
        NgbAccordionHeader,
        NgbAccordionToggle,
        NgbAccordionBody,
        NgbAccordionCollapse,
        NgbNavContent,
        NgbNav,
        NgbNavItem,
        NgbNavItemRole,
        NgbNavLinkButton,
        NgbNavLinkBase,
        NgbNavOutlet,
        User,
        NgbDropdown,
        NgbDropdownToggle,
        NgbDropdownMenu,
        NgbDropdownItem,
        NgbDropdownButtonItem,
        RouterLink,
        ReactiveFormsModule
    ]
})
export class FriendList implements OnDestroy, OnInit {

    private queueService: QueueService = inject(QueueService);

    private friendRequestService: FriendRequestService = inject(FriendRequestService);

    private friendService: FriendService = inject(FriendService);

    friendRequests!: Signal<FriendRequest[]>

    online!: Signal<UserData[]>

    offline!: Signal<UserData[]>

    active = 1;

    userFilter = new FormControl("");

    ngOnInit(): void {
        this.friendRequests = this.friendRequestService.friendRequests;
        this.online = this.friendService.online;
        this.offline = this.friendService.offline;
        this.userFilter.valueChanges.subscribe(value => {
            if (value != null) {
                this.friendService.filter(value);
            }
        });
    }

    ngOnDestroy(): void {

    }

    acceptFriendRequest(id: string): void {
        this.friendRequestService.acceptRequest(id).subscribe();
    }

    rejectFriendRequest(id: string): void {
        this.friendRequestService.rejectRequest(id).subscribe();
    }

    unfriend(id: string): void {
        this.friendService.unfriend(id).subscribe();
    }

    invite(id: string): void {
        this.queueService.invite(id).subscribe();
    }

}