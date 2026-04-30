import { Injectable, OnDestroy, OnInit } from "@angular/core";

@Injectable({
    providedIn: "root",
})
export class DebounceService {

    private timeoutId: number = -1;

    public debounce(func: () => void, delay: number = 0) {
        this.cancel();
        this.timeoutId = setTimeout(func, delay);
    }

    public cancel() {
        clearTimeout(this.timeoutId);
    }

}