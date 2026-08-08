import { Component, effect, inject, OnDestroy, OnInit, signal, Signal } from "@angular/core";
import { FormControl, ReactiveFormsModule } from "@angular/forms";
import { RouterLink } from "@angular/router";
import { NgbAccordionBody, NgbAccordionButton, NgbAccordionCollapse, NgbAccordionDirective, NgbAccordionHeader, NgbAccordionItem, NgbAccordionToggle, NgbDropdown, NgbDropdownButtonItem, NgbDropdownItem, NgbDropdownMenu, NgbDropdownToggle, NgbNav, NgbNavContent, NgbNavItem, NgbNavItemRole, NgbNavLinkBase, NgbNavLinkButton, NgbNavOutlet } from "@ng-bootstrap/ng-bootstrap";
import { Channel } from "../../api/ChannelApi";
import { FriendRequest } from "../../api/FriendRequestApi";
import { UserData } from "../../api/UserApi";
import { ChatService } from "../../services/ChatService";
import { FriendRequestService } from "../../services/FriendRequestService";
import { FriendService } from "../../services/FriendService";
import { MatchmakingService } from "../../services/MatchmakingService";
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

    private queueService: MatchmakingService = inject(MatchmakingService);

    private friendRequestService: FriendRequestService = inject(FriendRequestService);

    private friendService: FriendService = inject(FriendService);

    private chatService: ChatService = inject(ChatService);

    friendRequests!: Signal<FriendRequest[]>

    online!: Signal<UserData[]>

    offline!: Signal<UserData[]>

    dms = signal<{ [id: string]: Signal<Channel> } | null>(null);

    active = 1;

    userFilter = new FormControl("");

    constructor() {
        effect(() => {
            console.log(this.dms());
        });
    }

    ngOnInit(): void {
        this.friendRequests = this.friendRequestService.friendRequests;
        this.online = this.friendService.online;
        this.offline = this.friendService.offline;

        this.chatService.subscribeDms(dms => {
            this.dms.set(dms);
        })

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

    openDm(recipientId: string): void {
        this.chatService.openDmChannel(recipientId);
    }

}