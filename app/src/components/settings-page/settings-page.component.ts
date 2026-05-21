import { Component } from "@angular/core";
import { NgbNav, NgbNavContent, NgbNavItem, NgbNavLinkBase, NgbNavLinkButton, NgbNavOutlet } from "@ng-bootstrap/ng-bootstrap";
import { AvatarForm } from "../avatar-form/avatar-form.component";

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
        AvatarForm
    ],
})
export class SettingsPage {

    active: string = "profile";

}