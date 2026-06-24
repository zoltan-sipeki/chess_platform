import { Component, inject, input } from "@angular/core";
import { NgbDropdown, NgbDropdownItem, NgbDropdownMenu, NgbDropdownToggle } from "@ng-bootstrap/ng-bootstrap";
import { QueueService } from "../../services/QueueService";
import { QueueType } from "../../api/QueueApi";

@Component({
    selector: "play-menu",
    templateUrl: "play-menu.component.html",
    imports: [NgbDropdown, NgbDropdownToggle, NgbDropdownMenu, NgbDropdownItem]
})
export class PlayMenu {
    
    private queueService: QueueService = inject(QueueService);

    clazz = input<string>("");

    enqueue(queueType: QueueType): void {
        this.queueService.enqueue(queueType).subscribe();
    }
}