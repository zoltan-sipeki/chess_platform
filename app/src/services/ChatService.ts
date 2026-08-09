import { inject, Injectable, signal, WritableSignal } from "@angular/core";
import { Router } from "@angular/router";
import { map, Observable, tap } from "rxjs";
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

    private _channels = signal<Record<string, WritableSignal<Channel>>>({});

    private _messages = signal<Record<string, WritableSignal<Message[][]>>>({});

    private _dms = signal<Record<string, WritableSignal<Channel>>>({});

    private currentChannelId?: string;

    readonly channels = this._channels.asReadonly();

    readonly messages = this._messages.asReadonly();

    readonly dms = this._dms.asReadonly();

    constructor() {
        this.relayService.subscribe("MESSAGE_CREATED", e => {
            if (e.sender.id !== this.authService.getUserId()) {
                if (this.isChatWindowOpen(e.channelId)) {
                    this.debounceService.debounce(() => this.markAsRead(e.channelId), 500);
                } else {
                    this.updateUnreadCount(e.channelId);
                }
            }

            const messages = this._messages()[e.channelId];
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
            for (const channel of Object.values(this._channels())) {
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
            for (const channel of Object.values(this._channels())) {
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

            this._channels.set(channels);
            this._dms.set(dms);

        }));
    }

    fetchMessages(channelId: string): Observable<Message[][]> {
        const messages = this._messages()[channelId];
        if (messages != null) {
            return new Observable(observer => observer.next(messages()));
        }

        return this.channelApi.fetchMessages(channelId).pipe(
            map(result => this.groupMessages(result)),
            tap(result => {
                this._messages.update(messages => ({ ...messages, [channelId]: signal(result) }))
            }));
    }

    openDmChannel(recipientId: string): void {
        let channelId = null;
        for (const [id, channel] of Object.entries(this._channels())) {
            if (channel().type === "DM") {
                const recipient = channel().recipients[0].id;
                if (recipient === recipientId) {
                    channelId = id;
                }
            }

        }

        if (channelId == null) {
            this.channelApi.createChannel("DM", [recipientId]).subscribe(result => {
                const s = signal(result);

                this._channels.update(channels => ({ ...channels, [result.id]: s }));
                this._dms.update(dms => ({ ...dms, [recipientId]: s }));

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
        this._channels()[channelId]?.update(c => ({ ...c, unreadCount: 0 }));

        const messages = this._messages()[channelId];
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
        const channel = this._channels()[channelId];
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