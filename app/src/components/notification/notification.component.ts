import { Component } from "@angular/core";
import { NgbDropdown, NgbDropdownItem, NgbDropdownMenu, NgbDropdownToggle } from "@ng-bootstrap/ng-bootstrap";

@Component({
    selector: 'notification',
    templateUrl: 'notification.component.html',
    styleUrl: 'notification.component.css',
    imports: [NgbDropdown, NgbDropdownMenu, NgbDropdownToggle, NgbDropdownItem]
})
export class Notification {

}