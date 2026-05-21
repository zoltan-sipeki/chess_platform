import { inject, Injectable } from "@angular/core";
import { UserService } from "./UserService";
import { UserStoreService } from "./UserStoreService";
import { EventService } from "./EventService";

@Injectable({
    providedIn: "root",
})
export class DashboardService {

    private userService: UserService = inject(UserService);

    private userStore: UserStoreService = inject(UserStoreService);

    private eventService: EventService = inject(EventService);

    fetch(): void {
        this.userService.fetchDashboard().subscribe(dashboard => {
            this.userStore.setUser(dashboard.user);
        });
    }
}