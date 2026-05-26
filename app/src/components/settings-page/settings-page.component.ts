import { Component } from "@angular/core";
import { NgbNav, NgbNavContent, NgbNavItem, NgbNavLinkBase, NgbNavLinkButton, NgbNavOutlet } from "@ng-bootstrap/ng-bootstrap";
import { AvatarForm } from "../avatar-form/avatar-form.component";
import { PrivacyForm } from "../privacy-form/privacy-form.component";

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
        AvatarForm,
        PrivacyForm
    ],
})
export class SettingsPage {

    active: string = "profile";

    manageEmail(): void {
        window.open("http://keycloak:8080/realms/chess/account", "_blank");
    }

    changePassword(): void {
        window.open("http://keycloak:8080/realms/chess/account/account-security/signing-in", "_blank");
    }

}