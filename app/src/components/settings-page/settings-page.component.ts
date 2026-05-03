import { Component } from "@angular/core";
import { NgbNav, NgbNavContent, NgbNavItem, NgbNavLinkBase, NgbNavLinkButton, NgbNavOutlet } from "@ng-bootstrap/ng-bootstrap";

@Component({
    selector: "settings-page",
    templateUrl: "settings-page.component.html",
    imports: [
        NgbNavContent,
        NgbNav,
        NgbNavItem,
        NgbNavLinkButton,
        NgbNavLinkBase,
        NgbNavOutlet,
        NgbNavContent,
        NgbNav,
        NgbNavItem,
        NgbNavLinkButton,
        NgbNavLinkBase,
        NgbNavOutlet,
    ],
})
export class SettingsPage {
    active = "edit-profile";
}