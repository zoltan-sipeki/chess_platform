import { Injectable, signal } from "@angular/core";

@Injectable({
    providedIn: "root"
})
export class CountDownService {

    private _countDown = signal<number>(0);

    readonly countDown = this._countDown.asReadonly();

    private interval: number = -1;

    start(until: number): void {
        clearInterval(this.interval);
        let lastTime = Date.now();
        this._countDown.set(until - lastTime);
        this.interval = setInterval(() => {
            const now = Date.now();
            this._countDown.update(t => t - now + lastTime);
            lastTime = now;

            if (this._countDown() <= 0) {
                this.stop();
            }
        }, 1000);
    }

    stop(): void {
        clearInterval(this.interval);
        this._countDown.set(0);
    }
}