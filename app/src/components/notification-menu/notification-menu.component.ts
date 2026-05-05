import { Component, input } from "@angular/core";
import { RouterLink } from "@angular/router";
import { NgbDropdown, NgbDropdownItem, NgbDropdownMenu, NgbDropdownToggle } from "@ng-bootstrap/ng-bootstrap";
import { Notification } from "../notification/notification.component";

@Component({
    selector: "notification-menu",
    templateUrl: "notification-menu.component.html",
    styleUrl: "notification-menu.component.css",
    imports: [NgbDropdown, NgbDropdownToggle, NgbDropdownMenu, NgbDropdownItem, RouterLink, Notification]
})
export class NotificationMenu {

    clazz = input<string>("");

}