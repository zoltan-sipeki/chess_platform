import { inject, Injectable, signal } from "@angular/core";
import { Observable, tap } from "rxjs";
import { QueueApi, QueueType } from "./QueueApi";
import { MatchFoundRelayEvent, RelayService } from "./RelayService";

@Injectable({
    providedIn: "root",
})
export class QueueService {

    private api: QueueApi = inject(QueueApi);

    private relayService: RelayService = inject(RelayService);

    private interval: number = -1;

    private lastTime: number = 0;

    private _queue = signal<QueueType | null>(null);

    private _matchInfo = signal<MatchFoundRelayEvent | null>(null);

    private _timeInQueue = signal<number>(0);

    readonly queue = this._queue.asReadonly();

    readonly matchInfo = this._matchInfo.asReadonly();

    readonly timeInQueue = this._timeInQueue.asReadonly();

    constructor() {
        this.relayService.subscribe("MATCH_FOUND", e => {
            this._queue.set(null);
            this._matchInfo.set(e);
            this.stopTimer();
        });
    }

    enqueue(queueType: QueueType): Observable<void> {
        if (this._matchInfo() != null) {
            return new Observable<void>(observer => observer.next());
        }

        return this.api.enqueue(queueType).pipe(tap(() => {
            if (this._matchInfo() == null) {
                this._queue.set(queueType);
                this.startTimer();
            }
        }));
    }

    dequeue(): Observable<void> {
        return this.api.dequeue().pipe(tap(() => {
            this.stopTimer();
            this._queue.set(null);
        }));
    }

    invite(id: string): Observable<void> {
        if (this._queue() != null && this._matchInfo() != null) {
            return new Observable<void>(observer => observer.next());
        }

        return this.api.invite(id);
    }

    private startTimer(): void {
        clearInterval(this.interval);
        this.lastTime = Date.now();
        this.interval = setInterval(() => {
            const now = Date.now();
            this._timeInQueue.update(t => t + now - this.lastTime);
            this.lastTime = now;
        }, 1000);
    }

    private stopTimer(): void {
        clearInterval(this.interval);
        this._timeInQueue.set(0);
        this.lastTime = 0;
    }

    deleteMatchInfo(): void {
        this._matchInfo.set(null);
    }
}