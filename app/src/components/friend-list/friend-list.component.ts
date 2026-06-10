import { Component, inject, OnDestroy, OnInit, Signal } from "@angular/core";
import { RouterLink } from "@angular/router";
import { NgbAccordionBody, NgbAccordionButton, NgbAccordionCollapse, NgbAccordionDirective, NgbAccordionHeader, NgbAccordionItem, NgbAccordionToggle, NgbDropdown, NgbDropdownButtonItem, NgbDropdownItem, NgbDropdownMenu, NgbDropdownToggle, NgbNav, NgbNavContent, NgbNavItem, NgbNavItemRole, NgbNavLinkBase, NgbNavLinkButton, NgbNavOutlet } from "@ng-bootstrap/ng-bootstrap";
import { FriendRequest } from "../../services/FriendRequestApi";
import { FriendRequestService } from "../../services/FriendRequestService";
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
        RouterLink
    ]
})
export class FriendList implements OnDestroy, OnInit {

    private friendRequestService: FriendRequestService = inject(FriendRequestService);

    friendRequests!: Signal<FriendRequest[]>

    active = 1;

    ngOnInit(): void {
        this.friendRequests = this.friendRequestService.friendRequests;
    }

    ngOnDestroy(): void {

    }

    acceptFriendRequest(id: string): void {
        this.friendRequestService.acceptFriendRequest(id).subscribe();
    }

    rejectFriendRequest(id: string): void {
        this.friendRequestService.rejectFriendRequest(id).subscribe();
    }

}