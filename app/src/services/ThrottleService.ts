import { Injectable } from "@angular/core";

@Injectable({
    providedIn: "root",
})
export class ThrottleService {

    private timeoutId: number = -1;

    private canRun: boolean = true;

    public throttle(func: () => void, delay: number = 0) {
        if (this.canRun) {
            func();
            this.canRun = false;
            this.timeoutId = setTimeout(() => this.canRun = true, delay);
        }
    }

    public cancel() {
        clearTimeout(this.timeoutId);
    }

}