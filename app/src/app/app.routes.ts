import { Routes } from '@angular/router';
import { Dashboard } from '../components/dashboard/dashboard.component';
import { FriendsPage } from '../components/friends-page/friends-page.component';
import { Homepage } from '../components/homepage/homepage.component';
import { Leaderboard } from '../components/leaderboard/leaderboard.component';
import { Login } from '../components/login/login.component';
import { MatchHistoryPage } from '../components/match-history-page/match-history-page.component';
import { NotificationPage } from '../components/notification-page/notification-page.component';
import { SettingsPage } from '../components/settings-page/settings-page.component';
import { UserProfilePage } from '../components/user-profile-page/user-profile-page.component';
import { UserSearchPage } from '../components/user-search-page/user-search-page.component';

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
                path: "users/:id/friends",
                component: FriendsPage
            },
            {
                path: "users/:id/history",
                component: MatchHistoryPage
            },
            {
                path: "users/:id",
                component: UserProfilePage
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
