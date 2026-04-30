export interface UserData {
    id: string;
    displayName: string;
    avatar: string;
}

export interface UserSearchResult {
    hasMore: boolean,
    users: UserData[]
}