import type { IFrame } from "@stomp/stompjs";

export interface SendMessageRequest {
  sender: string;
  content: string;
}

export interface Renderer {
  appendToChat(message: SendMessageRequest): void;
  appendHistory(message: SendMessageRequest[]): void;
  getDraft(): SendMessageRequest;
  clearDraft(): void;
  onSend(callback: (draft: SendMessageRequest) => void): void;
}

export type OnConnectHandler = (frame: IFrame) => Promise<void>;
export type OnSendHandler = (draft: SendMessageRequest) => void;
