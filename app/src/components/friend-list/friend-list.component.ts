import { Component } from "@angular/core";
import { NgbAccordionBody, NgbAccordionButton, NgbAccordionCollapse, NgbAccordionDirective, NgbAccordionHeader, NgbAccordionItem, NgbAccordionToggle, NgbDropdown, NgbDropdownButtonItem, NgbDropdownItem, NgbDropdownMenu, NgbDropdownToggle, NgbNav, NgbNavContent, NgbNavItem, NgbNavItemRole, NgbNavLinkBase, NgbNavLinkButton, NgbNavOutlet } from "@ng-bootstrap/ng-bootstrap";
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
        NgbDropdownButtonItem
    ]
})
export class FriendList {

    active = 1;

}