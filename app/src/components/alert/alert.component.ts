import { Component, inject, OnDestroy, OnInit, signal, viewChild } from "@angular/core";
import { NgbAlert } from "@ng-bootstrap/ng-bootstrap";
import { AlertEvent, AlertType, EventService } from "../../services/EventService";

@Component({
    selector: "alert",
    templateUrl: "./alert.component.html",
    imports: [NgbAlert]
})
export class AlertComponent implements OnInit, OnDestroy {

    private eventService: EventService = inject(EventService);

    private alert = viewChild<NgbAlert>("alert");

    private timeout: number = -1;

    alertDetails = signal<{ type: AlertType, message: string } | null>(null);

    ngOnInit(): void {
        this.eventService.addEventListener("alert", this.onAlert);
    }

    ngOnDestroy(): void {
        this.eventService.removeEventListener("alert", this.onAlert);
        clearTimeout(this.timeout);
    }

    onAlert = (e: AlertEvent): void => {
        this.alertDetails.set(e.details);
        clearTimeout(this.timeout);
        this.timeout = setTimeout(() => this.alert()?.close(), 5000);
    }

    onAlertClose(): void {
        clearTimeout(this.timeout);
        this.alertDetails.set(null);
    }
}