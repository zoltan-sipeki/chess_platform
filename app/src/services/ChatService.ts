import { inject, Injectable, Signal, signal, WritableSignal } from "@angular/core";
import { Router } from "@angular/router";
import { BehaviorSubject, Observable, Subscription, takeWhile, tap } from "rxjs";
import { Channel, ChannelApi, Message } from "../api/ChannelApi";
import { AuthService } from "./AuthService";
import { DebounceService } from "./DebounceService";
import { RelayService } from "./RelayService";


@Injectable({ providedIn: 'root' })
export class ChatService {

    private channelApi: ChannelApi = inject(ChannelApi);

    private relayService: RelayService = inject(RelayService);

    private authService: AuthService = inject(AuthService);

    private debounceService: DebounceService = inject(DebounceService);

    private router: Router = inject(Router);

    private channels = new BehaviorSubject<{ [id: string]: WritableSignal<Channel> }>({});

    private messages = new BehaviorSubject<{ [id: string]: WritableSignal<Message[][]> }>({});

    private dms = new BehaviorSubject<{ [id: string]: WritableSignal<Channel> }>({});

    private currentChannelId?: string;

    constructor() {
        this.relayService.subscribe("MESSAGE_CREATED", e => {
            if (e.sender.id !== this.authService.getUserId()) {
                if (this.isChatWindowOpen(e.channelId)) {
                    this.debounceService.debounce(() => this.markAsRead(e.channelId), 500);
                } else {
                    this.updateUnreadCount(e.channelId);
                }
            }

            const messages = this.messages.getValue()[e.channelId];
            if (messages == null) {
                return;
            }

            messages.update(m => {
                const r = [...m];
                this.addMessage(r, e);
                return r;
            });
        });

        this.relayService.subscribe("PRESENCE_CHANGED", e => {
            for (const channel of Object.values(this.channels.getValue())) {
                if (channel().recipients.find(r => r.id === e.userId) != null) {
                    channel.update(channel => {
                        return {
                            ...channel,
                            recipients: channel.recipients.map(r => r.id === e.userId ? { ...r, presence: e.presence } : r)
                        };
                    });
                }
            }
        });

        this.relayService.subscribe("ACTIVITY_CHANGED", e => {
            for (const channel of Object.values(this.channels.getValue())) {
                if (channel().recipients.find(r => r.id === e.userId) != null) {
                    channel.update(channel => {
                        return {
                            ...channel,
                            recipients: channel.recipients.map(r => r.id === e.userId ? { ...r, activity: e.activity } : r)
                        };
                    });
                }
            }
        });
    }

    subscribeChannels(channelId: string, callback: (channel: Signal<Channel>) => void): void {
        this.channels.pipe(takeWhile(channels => channels[channelId] == null, true)).subscribe(channels => {
            if (channels[channelId] != null) {
                callback(channels[channelId]);
            }
        })
    }

    subscribeMessages(channelId: string, callback: (messages: Signal<Message[][]>) => void): void {
        this.messages.pipe(takeWhile(messages => messages[channelId] == null, true)).subscribe(messages => {
            if (messages[channelId] != null) {
                callback(messages[channelId]);
            }
        });
    }

    subscribeDms(callback: (dms: { [id: string]: Signal<Channel> }) => void): Subscription {
        return this.dms.subscribe(dms => {
            callback(dms);
        });
    }

    fetchChannels(): Observable<Channel[]> {
        return this.channelApi.fetchChannels().pipe(tap(result => {
            const channels: { [id: string]: WritableSignal<Channel> } = {};
            const dms: { [id: string]: WritableSignal<Channel> } = {};

            for (const channel of result) {
                const s = signal(channel);
                channels[channel.id] = s;

                if (channel.type === "DM") {
                    dms[channel.recipients[0].id] = s;
                }
            }

            this.channels.next(channels);
            this.dms.next(dms);

        }));
    }

    fetchMessages(channelId: string): void {
        if (this.messages.getValue()[channelId] != null) {
            return;
        }

        this.channelApi.fetchMessages(channelId).pipe(tap(result => {
            const messages = this.messages.getValue();
            messages[channelId] = signal(this.groupMessages(result));
            this.messages.next(messages);
        })).subscribe();
    }

    openDmChannel(recipientId: string): void {
        let channelId = null;
        for (const [id, channel] of Object.entries(this.channels.getValue())) {
            if (channel().type === "DM") {
                const recipient = channel().recipients[0].id;
                if (recipient === recipientId) {
                    channelId = id;
                }
            }

        }

        if (channelId == null) {
            this.channelApi.createChannel("DM", [recipientId]).subscribe(result => {
                const channels = this.channels.getValue();
                channels[result.id] = signal(result);

                this.channels.next(channels);

                const dms = this.dms.getValue();
                dms[recipientId] = signal(result);

                this.dms.next(dms);

                this.router.navigate([`/channels/${result.id}`]);
            });
        }
        else {
            this.router.navigate([`/channels/${channelId}`]);
        }

    }

    sendMessage(channelId: string, message: string): void {
        this.channelApi.sendMessage(channelId, message).subscribe();
    }


    markAsRead(channelId: string): void {
        this.channels.getValue()[channelId]?.update(c => ({ ...c, unreadCount: 0 }));

        const messages = this.messages.getValue()[channelId];
        if (messages == null) {
            return;
        }

        const lastGroup = messages()[messages().length - 1];
        if (lastGroup == null) {
            return;
        }

        const lastMessage = lastGroup[lastGroup.length - 1];
        if (lastMessage == null) {
            return;
        }

        this.channelApi.updateUnread(channelId, lastMessage.sequenceNumber).subscribe();
    }

    setCurrentChannelId(channelId?: string): void {
        this.currentChannelId = channelId;
    }

    broadcastTyping(channelId: string): void {
        this.channelApi.broadcastTyping(channelId).subscribe();
    }

    private groupMessages(messages: Message[]): Message[][] {
        const res: Message[][] = [];
        if (messages.length === 0) {
            return res;
        }

        res.push([messages[0]]);
        for (let i = 1; i < messages.length; i++) {
            this.addMessage(res, messages[i]);
        }

        return res;
    }

    private addMessage(messages: Message[][], message: Message): void {
        if (messages.length === 0) {
            messages.push([message]);
            return;
        }

        const lastGroup = messages[messages.length - 1];
        const lastMessage = lastGroup[lastGroup.length - 1];

        if (lastMessage == null) {
            messages.push([message]);
            return;
        }

        const lastDate = new Date(lastMessage.createdAt);
        const currentDate = new Date(message.createdAt);

        if (lastDate.getHours() === currentDate.getHours() && lastDate.getMinutes() === currentDate.getMinutes() && lastMessage.sender.id === message.sender.id) {
            lastGroup.push(message);
        }
        else {
            messages.push([message]);
        }
    }

    private updateUnreadCount(channelId: string): void {
        const channel = this.channels.getValue()[channelId];
        if (channel == null) {
            return;
        }

        channel.update(c => {
            return { ...c, unreadCount: c.unreadCount + 1 };
        })
    }

    private isChatWindowOpen(channelId: string): boolean {
        return this.currentChannelId === channelId;
    }
}