import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
    name: "timeAgo"
})
export class TimeAgoPipe implements PipeTransform {

    private units = [
        { ms: 31_536_000_000, unit: "year(s)" },
        { ms: 2_592_000_000, unit: "month(s)" },
        { ms: 604_800_000, unit: "week(s)" },
        { ms: 86_400_000, unit: "day(s)" },
        { ms: 3_600_000, unit: "hour(s)" },
        { ms: 60_000, unit: "minute(s)" },
        { ms: 1000, unit: "second(s)" },
    ];

    transform(date: Date): string {
        const ms = date.getMilliseconds();
        for (const unit of this.units) {
            const result = Math.floor(ms / unit.ms);
            if (result > 0) {
                return result + " " + unit.unit + " ago";
            }
        }
        return "";
    }
}