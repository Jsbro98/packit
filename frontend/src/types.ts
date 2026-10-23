import type { Client, IFrame } from "@stomp/stompjs";

export interface Message {
  sender: string;
  content: string;
}

export interface Renderer {
  appendToChat(message: Message): void;
  appendHistory(message: Message[]): void;
  bindSubmitListener(client: Client): void;
}

export type OnConnectHandler = (frame: IFrame) => Promise<void>;
