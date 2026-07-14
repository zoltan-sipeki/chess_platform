import { Routes } from '@angular/router';
import { Login } from '../components/login/login.component';
import { UserProfilePage } from '../components/user-profile-page/user-profile-page.component';
import { UserSearchPage } from '../components/user-search-page/user-search-page.component';
import { ChessPage } from '../pages/chess-page/chess-page.component';
import { Dashboard } from '../pages/dashboard/dashboard.component';
import { FriendsPage } from '../pages/friends-page/friends-page.component';
import { Homepage } from '../pages/homepage/homepage.component';
import { Leaderboard } from '../pages/leaderboard/leaderboard.component';
import { MatchHistoryPage } from '../pages/match-history-page/match-history-page.component';
import { NotificationPage } from '../pages/notification-page/notification-page.component';
import { SettingsPage } from '../pages/settings-page/settings-page.component';

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
        path: "t/:target",
        component: ChessPage
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
