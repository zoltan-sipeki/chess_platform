import { NgTemplateOutlet, TitleCasePipe } from "@angular/common";
import { Component, effect, ElementRef, inject, OnDestroy, OnInit, Signal, signal, viewChild } from "@angular/core";
import { NgbToast, NgbToastHeader } from "@ng-bootstrap/ng-bootstrap";
import { CurrentMatch, QueueType } from "../../api/MatchmakingApi";
import { TimeFormatPipe } from "../../pipes/TimeFormatPipe";
import { MatchmakingService } from "../../services/MatchmakingService";
import { UserService } from "../../services/UserService";
import { UserData } from "../../api/UserApi";

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

    private matchmakingService: MatchmakingService = inject(MatchmakingService);

    private userService: UserService = inject(UserService);

    private elementRef: ElementRef = inject(ElementRef);

    private queueToast = viewChild("queueToast", { read: ElementRef });

    private matchFoundToast = viewChild("matchFoundToast", { read: ElementRef });

    private dragging: boolean = false;

    cursor = signal<string>("grab");

    top = signal<number>(0);

    left = signal<number>(0);

    queue!: Signal<QueueType | null>;

    currentMatch!: Signal<CurrentMatch | null>;

    timeInQueue!: Signal<number>;

    currentUser!: Signal<UserData>;

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
        this.queue = this.matchmakingService.queue;
        this.currentMatch = this.matchmakingService.currentMatch;
        this.timeInQueue = this.matchmakingService.timeInQueue;
        this.currentUser = this.userService.currentUser;
        
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
        this.matchmakingService.dequeue().subscribe();
    }

    declineCurrentMatch(): void {
        this.matchmakingService.declineCurrentMatch().subscribe();
    }
}