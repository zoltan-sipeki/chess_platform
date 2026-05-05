import { Component } from "@angular/core";
import { Notification } from "../notification/notification.component";
import { NgbNav, NgbNavConfig, NgbNavContent, NgbNavItem, NgbNavLink, NgbNavLinkButton, NgbNavOutlet } from "@ng-bootstrap/ng-bootstrap";

@Component({
    selector: "notification-page",
    templateUrl: "notification-page.component.html",
    imports: [Notification, NgbNav, NgbNavContent, NgbNavItem, NgbNavOutlet, NgbNavLinkButton]
})
export class NotificationPage {
    active = "all";
}