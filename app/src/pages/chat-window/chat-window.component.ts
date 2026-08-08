import { Component, computed, effect, ElementRef, inject, OnDestroy, OnInit, signal, Signal, viewChild } from '@angular/core';
import { FormControl, ReactiveFormsModule } from "@angular/forms";
import { ActivatedRoute } from "@angular/router";
import { NgbDropdown, NgbDropdownMenu, NgbDropdownToggle } from "@ng-bootstrap/ng-bootstrap";
import { Channel, Message } from '../../api/ChannelApi';
import { UserData } from '../../api/UserApi';
import { AvatarComponent } from "../../components/avatar/avatar.component";
import { User } from "../../components/user/user.component";
import { MessageTimestampPipe } from "../../pipes/MessageTimestampPipe";
import { ChatService } from "../../services/ChatService";
import { DebounceService } from '../../services/DebounceService';
import { ChannelTypingRelayEvent, MessageCreatedRelayEvent, RelayService } from '../../services/RelayService';
import { ThrottleService } from '../../services/ThrottleService';
import { emojis, replaceEmojis } from './emoji-utils';

@Component({
    selector: "chat-window",
    templateUrl: "./chat-window.component.html",
    styleUrl: "./chat-window.component.css",
    standalone: true,
    providers: [DebounceService, ThrottleService],
    imports: [User, AvatarComponent, ReactiveFormsModule, MessageTimestampPipe, NgbDropdown, NgbDropdownMenu, NgbDropdownToggle],
})
export class ChatWindowComponent implements OnInit, OnDestroy {

    private chatService: ChatService = inject(ChatService);

    private debounceService: DebounceService = inject(DebounceService);

    private throttleService: ThrottleService = inject(ThrottleService);

    private relayService: RelayService = inject(RelayService);

    private route: ActivatedRoute = inject(ActivatedRoute);

    private textArea = viewChild('textArea', { read: ElementRef });

    private chatWindow = viewChild('chatWindow', { read: ElementRef });

    emojis = Object.entries(emojis);

    channel = signal<Signal<Channel> | null>(null);

    messages = signal<Signal<Message[][]> | null>(null);

    unreadCount = signal<number>(0);

    typing = signal<UserData | null>(null);

    unreadIndex = computed(() => {
        if (this.unreadCount() === 0) {
            return -1;
        }

        let count = 0;
        const messages = this.messages()?.();
        if (messages == null) {
            return -1;
        }

        for (let i = messages.length - 1; i >= 0; --i) {
            const group = messages[i];
            count += group.length;
            if (count >= this.unreadCount()) {
                return i;
            }
        }

        return -1;
    });

    messageCount = computed(() => this.countMessages());

    message = new FormControl("");

    constructor() {
        effect(() => {
            if (this.messages()?.()) {
                setTimeout(() => this.scrollToBottom());
            }
        });
    }

    ngOnInit(): void {
        this.route.params.subscribe(params => {
            const channelId = params["id"];
            if (channelId == null) {
                return;
            }

            this.chatService.setCurrentChannelId(channelId);

            this.chatService.fetchMessages(channelId);

            this.chatService.subscribeChannels(channelId, channel => {
                this.channel.set(channel);
                this.unreadCount.set(channel().unreadCount);
            });

            this.chatService.subscribeMessages(channelId, messages => {
                this.messages.set(messages);
                this.chatService.markAsRead(channelId);
            })

        });

        this.message.valueChanges.subscribe(value => {
            if (value == null) {
                return;
            }

            this.message.setValue(replaceEmojis(value), { emitEvent: false });
        });

        this.relayService.subscribe("CHANNEL_TYPING", this.onChannelTypingRelayEvent);
        this.relayService.subscribe("MESSAGE_CREATED", this.onMessageCreatedRelayEvent);
    }

    ngOnDestroy(): void {
        this.chatService.setCurrentChannelId();
        this.relayService.unsubscribe("CHANNEL_TYPING", this.onChannelTypingRelayEvent);
        this.relayService.unsubscribe("MESSAGE_CREATED", this.onMessageCreatedRelayEvent);
        this.throttleService.cancel();
        this.debounceService.cancel();
    }

    onKeyDown(event: KeyboardEvent): void {
        if (event.key === "Enter" && !event.shiftKey) {
            event.preventDefault();
            this.sendMessage();
            this.debounceService.cancel();
        }
        else {
            const channelId = this.channel()?.().id;
            if (channelId != null) {
                this.throttleService.throttle(() => this.chatService.broadcastTyping(channelId), 5000);
            }
        }
    }

    sendMessage(): void {
        const channelId = this.channel()?.().id;
        if (channelId && this.message.value) {
            this.chatService.sendMessage(channelId, this.message.value);
            this.message.setValue("");
        }
    }

    insertEmoji(emoji: string): void {
        const cursorPosition = this.textArea()?.nativeElement.selectionStart

        let text = this.message.value ?? "";
        text = text.slice(0, cursorPosition) + emoji + text.slice(cursorPosition);
        this.message.setValue(text, { emitEvent: false });
    }

    private onChannelTypingRelayEvent = (e: ChannelTypingRelayEvent): void => {
        if (e.channelId === this.channel()?.().id) {
            const user = this.channel()?.().recipients.find(r => r.id === e.userId);
            if (user) {
                this.typing.set(user);
                this.debounceService.debounce(() => this.typing.set(null), 5000);
            }
        }
    }

    private onMessageCreatedRelayEvent = (e: MessageCreatedRelayEvent): void => {
        if (e.channelId === this.channel()?.().id && e.sender.id === this.typing()?.id) {
            this.typing.set(null);
        }
    }

    private scrollToBottom(): void {
        const chat = this.chatWindow()?.nativeElement;
        if (chat) {
            chat.scrollTop = chat.scrollHeight;
        }
    }

    private countMessages(): number {
        let count = 0;
        const messages = this.messages()?.();
        if (messages == null) {
            return count;
        }

        for (const group of messages) {
            count += group.length;
        }
        return count;
    }

}