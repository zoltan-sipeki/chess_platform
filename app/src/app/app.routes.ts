import { Routes } from '@angular/router';
import { Login } from '../components/login/login.component';
import { Dashboard } from '../components/dashboard/dashboard.component';
import { Homepage } from '../components/homepage/homepage.component';
import { UserSearchPage } from '../components/user-search-page/user-search-page.component';
import { UserProfile } from '../components/user-profile/user-profile.component';
import { SettingsPage } from '../components/settings-page/settings-page.component';
import { Leaderboard } from '../components/leaderboard/leaderboard.component';
import { NotificationPage } from '../components/notification-page/notification-page.component';
import { GameHistoryPage } from '../components/game-history-page/game-history-page.component';

export const routes: Routes = [
    {
        path: "login",
        component: Login
    },
    {
        path: "",
        component: Homepage
    },
    {
        path: "dashboard",
        component: Dashboard,
        children: [
            {
                path: "users/:id/history",
                component: GameHistoryPage
            },
            {
                path: "users/:id",
                component: UserProfile
            },
            {
                path: "users",
                component: UserSearchPage
            },
            {
                path: "settings",
                component: SettingsPage
            },
            {
                path: "leaderboard",
                component: Leaderboard
            },
            {
                path: "notifications",
                component: NotificationPage
            }]
    }];
