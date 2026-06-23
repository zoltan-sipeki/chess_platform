import { NgTemplateOutlet, TitleCasePipe } from "@angular/common";
import { Component, effect, ElementRef, inject, OnDestroy, OnInit, Signal, signal, viewChild } from "@angular/core";
import { NgbToast, NgbToastHeader } from "@ng-bootstrap/ng-bootstrap";
import { TimeFormatPipe } from "../../pipes/TimeFormatPipe";
import { QueueType } from "../../services/QueueApi";
import { QueueService } from "../../services/QueueService";
import { MatchFoundRelayEvent } from "../../services/RelayService";

@Component({
    selector: "queue-toast",
    templateUrl: "queue-toast.component.html",
    standalone: true,
    imports: [NgbToast, NgbToastHeader, NgTemplateOutlet, TimeFormatPipe, TitleCasePipe],
    host: {
        "class": "position-fixed",
        "[style.cursor]": "cursor()",
        "[style.top.px]": "top()",
        "[style.left.px]": "left()",
        "(mousedown)": "startDragging($event)"
    }
})
export class QueueToastComponent implements OnInit, OnDestroy {

    private queueService: QueueService = inject(QueueService);

    private elementRef: ElementRef = inject(ElementRef);

    private queueToast = viewChild("queueToast", { read: ElementRef });

    private matchFoundToast = viewChild("matchFoundToast", { read: ElementRef });

    private dragging: boolean = false;

    cursor = signal<string>("grab");

    top = signal<number>(0);

    left = signal<number>(0);

    queue!: Signal<QueueType | null>;

    matchInfo!: Signal<MatchFoundRelayEvent | null>;

    timeInQueue!: Signal<number>;

    constructor() {
        effect(() => {
            if (this.queueToast() != null || this.matchFoundToast() != null) {
                setTimeout(() => {
                    const { width, height } = this.elementRef.nativeElement.getBoundingClientRect();
                    this.top.set(window.innerHeight - height - 10);
                    this.left.set(window.innerWidth - width - 10);
                });
            }
        });
    }

    ngOnInit(): void {
        this.queue = this.queueService.queue;
        this.matchInfo = this.queueService.matchInfo;
        this.timeInQueue = this.queueService.timeInQueue;
        document.addEventListener("mousemove", this.drag);
        document.addEventListener("mouseup", this.stopDragging);
    }

    ngOnDestroy(): void {
        document.removeEventListener("mousemove", this.drag);
    }

    startDragging(e: MouseEvent): void {
        this.dragging = true;
        this.cursor.set("grabbing");
    }

    stopDragging = (e: MouseEvent): void => {
        this.dragging = false;
        this.cursor.set("grab");
    }

    drag = (e: MouseEvent): void => {
        if (!this.dragging) {
            return;
        }

        this.left.set(this.left() + e.movementX);
        this.top.set(this.top() + e.movementY);
    }

    dequeue(): void {
        this.queueService.dequeue().subscribe();
    }

    deleteMatchInfo(): void {
        this.queueService.deleteMatchInfo();
    }
}