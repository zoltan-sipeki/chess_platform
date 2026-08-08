import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
    name: 'messageTimestamp'
})
export class MessageTimestampPipe implements PipeTransform {
    transform(timestamp: string) {
        const date = new Date(timestamp);

        const time = this.removeSeconds(date.toLocaleTimeString());
        const dayDiff = new Date().getDay() - date.getDay();
        if (dayDiff === 0) {
            return time;
        }

        if (dayDiff === 1) {
            return 'Yesterday at ' + time;
        }

        return this.removeSeconds(date.toLocaleString());
    }

    private isDigit(char: string): boolean {
        return char >= '0' && char <= '9';
    }

    private removeSeconds(timestamp: string) {
        let result = "";
        let colonCount = 0;
        for (let i = 0; i < timestamp.length; ++i) {
            if (timestamp[i] === ':') {
                ++colonCount;
                if (colonCount === 2) {
                    continue;
                }
            }
            if (colonCount < 2) {
                result += timestamp[i];
            }
            else if (!this.isDigit(timestamp[i])) {
                result += timestamp[i];
            }
        }
        return result;
    }
}