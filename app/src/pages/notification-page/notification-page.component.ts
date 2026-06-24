import { Component, OnDestroy, OnInit } from "@angular/core";
import { NotificationListComponent } from "../../components/notification-list/notification-list.component";

@Component({
    selector: "notification-page",
    templateUrl: "notification-page.component.html",
    imports: [NotificationListComponent],
    standalone: true
})
export class NotificationPage implements OnInit, OnDestroy {

    ngOnInit(): void {
    }

    ngOnDestroy(): void {
    }
}