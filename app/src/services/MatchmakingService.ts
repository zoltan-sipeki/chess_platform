import { inject, Injectable, signal } from "@angular/core";
import { Observable, tap } from "rxjs";
import { CurrentMatch, MatchmakingApi, QueueType } from "../api/MatchmakingApi";
import { RelayService } from "./RelayService";

@Injectable({
    providedIn: "root",
})
export class MatchmakingService {

    private api: MatchmakingApi = inject(MatchmakingApi);

    private relayService: RelayService = inject(RelayService);

    private interval: number = -1;

    private lastTime: number = 0;

    private _queue = signal<QueueType | null>(null);

    private _currentMatch = signal<CurrentMatch | null>(null);

    private _timeInQueue = signal<number>(0);

    readonly queue = this._queue.asReadonly();

    readonly currentMatch = this._currentMatch.asReadonly();

    readonly timeInQueue = this._timeInQueue.asReadonly();

    constructor() {
        this.relayService.subscribe("MATCH_FOUND", e => {
            this._queue.set(null);
            this._currentMatch.set({ ...e });
            this.stopTimer();
        });
    }

    fetchCurrentMatch(): Observable<CurrentMatch> {
        return this.api.fetchCurrentMatch().pipe(tap({
            next: (matchInfo) => this._currentMatch.set(matchInfo),
        }));
    }

    enqueue(queueType: QueueType): Observable<void> {
        if (this._currentMatch() != null) {
            return new Observable<void>(observer => observer.next());
        }

        return this.api.enqueue(queueType).pipe(tap(() => {
            if (this._currentMatch() == null) {
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
        if (this._queue() != null && this._currentMatch() != null) {
            return new Observable<void>(observer => observer.next());
        }

        return this.api.invite(id);
    }

    declineCurrentMatch(): Observable<void> {
        const matchInfo = this._currentMatch();
        this._currentMatch.set(null);

        return this.api.declineCurrentMatch().pipe(tap({
            error: () => {
                this._currentMatch.set(matchInfo);
            }
        }));
    }

    clearCurrentMatch(): void {
        this._currentMatch.set(null);
        this.stopTimer();
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


}