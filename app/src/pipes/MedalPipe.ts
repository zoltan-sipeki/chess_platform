import { Pipe, PipeTransform } from "@angular/core";

@Pipe({ name: "medal" })
export class MedalPipe implements PipeTransform {
    transform(value: number): string {
        if (value === 1) {
            return "🥇 " + value;
        }

        if (value === 2) {
            return "🥈 " + value;
        }

        if (value === 3) {
            return "🥉 " + value;
        }

        return value.toString();
    }
}