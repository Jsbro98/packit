import type { IFrame } from "@stomp/stompjs";

export interface Message {
  sender: string;
  content: string;
}

export interface Renderer {
  appendToChat(message: Message): void;
  appendHistory(message: Message[]): void;
  getDraft(): Message;
  clearDraft(): void;
  onSend(callback: (draft: Message) => void): void;
}

export type OnConnectHandler = (frame: IFrame) => Promise<void>;
export type OnSendHandler = (draft: Message) => void;
