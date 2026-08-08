import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UserData } from './UserApi';

export interface Message {
    id: string;
    channelId: string;
    sender: UserData;
    sequenceNumber: number;
    content: string;
    createdAt: string;
    lastEditedAt?: string;
}

export type ChannelType = "DM" | "GROUP";

export interface Channel {
    id: string;
    name: string;
    type: ChannelType;
    lastReadMessageSequence: number;
    unreadCount: number;
    recipients: UserData[];
}

@Injectable({
    providedIn: 'root',
})
export class ChannelApi {

    private readonly BASE_URL = '/api/channels';

    private readonly http: HttpClient = inject(HttpClient);

    fetchChannels(): Observable<any> {
        return this.http.get<any>(this.BASE_URL);
    }

    createChannel(type: ChannelType, members: string[]): Observable<any> {
        return this.http.post<any>(this.BASE_URL, { type, members });
    }

    fetchMessages(id: string): Observable<Message[]> {
        return this.http.get<any>(`${this.BASE_URL}/${id}/messages`);
    }

    sendMessage(id: string, message: string): Observable<Message> {
        return this.http.post<any>(`${this.BASE_URL}/${id}/messages`, { content: message });
    }

    sendTyping(id: string): Observable<void> {
        return this.http.post<void>(
            `${this.BASE_URL}/${id}/typing`, {}
        );
    }

    clearHistory(id: string): Observable<void> {
        return this.http.delete<void>(
            `${this.BASE_URL}/${id}/members/me/history`
        );
    }

    updateUnread(id: string, sequenceNumber: number): Observable<void> {
        return this.http.put<void>(
            `${this.BASE_URL}/${id}/members/me/unread`,
            { sequenceNumber }
        );
    }

    broadcastTyping(channelId: string): Observable<void> {
        return this.http.post<void>(
            `${this.BASE_URL}/${channelId}/typing`,
            {}
        );
    }
}