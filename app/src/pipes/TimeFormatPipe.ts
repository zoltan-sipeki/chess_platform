import { Pipe, PipeTransform } from "@angular/core";

export type TimeFormatInputUnit = "ms" | "s";

@Pipe({
    name: 'timeFormat'
})
export class TimeFormatPipe implements PipeTransform {

    private hoursMs = 3_600_000;

    private minutesMs = 60_000;

    private secondsMs = 1000;

    transform(value: number, unit: TimeFormatInputUnit): string {
        if (unit === "s") {
            value *= 1000;
        }

        const hours = Math.floor(value / this.hoursMs);
        const minutes = Math.floor((value % this.hoursMs) / this.minutesMs);
        const seconds = Math.floor((value % this.hoursMs % this.minutesMs) / this.secondsMs);

        const result = `${minutes.toString().padStart(2, "0")}:${seconds.toString().padStart(2, "0")}`;

        if (hours > 0) {
            return `${hours.toString().padStart(2, "0")}:${result}`;
        }

        return result;
    }
}