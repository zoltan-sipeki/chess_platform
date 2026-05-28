import { inject, Injectable } from "@angular/core";
import { UserService } from "./UserService";
import { UserStore } from "./UserStore";
import { EventService } from "./EventService";

@Injectable({
    providedIn: "root",
})
export class DashboardService {

    private userService: UserService = inject(UserService);

    private userStore: UserStore = inject(UserStore);

    private eventService: EventService = inject(EventService);

    fetch(): void {
        this.userService.fetchDashboard().subscribe(dashboard => {
            this.userStore.setUser(dashboard.user);
        });
    }
}