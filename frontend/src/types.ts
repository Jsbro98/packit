import type { IFrame } from "@stomp/stompjs";

/** Payload the client sends to `/app/send`. */
export interface SendMessageRequest {
  sender: string;
  content: string;
}

/** A chat message as broadcast by the server. Mirrors the backend `ChatMessage` record. */
export interface ChatMessage {
  /** UUID assigned by the server. Unique per message, safe for deduping. */
  id: string;
  /** ISO-8601 instant stamped by the server. Parse with `new Date(...)`. */
  timestamp: string;
  sender: string;
  content: string;
}

/** DOM object for the chat UI handling. Holds no connection logic. */
export interface Renderer {
  /** Appends one live message to the end of the chat. */
  appendToChat(message: ChatMessage): void;
  /** Appends a batch of messages, oldest first. */
  appendHistory(messages: ChatMessage[]): void;
  /** Reads the current username and message box, trimmed. */
  getDraft(): SendMessageRequest;
  /** Empties the message box (not the username). */
  clearDraft(): void;
  /** Registers the send handler. Not called when sender or content is empty. */
  onSend(callback: (draft: SendMessageRequest) => void): void;
}

/** Runs on every (re)connect, not just the first. */
export type OnConnectHandler = (frame: IFrame) => Promise<void>;
export type OnSendHandler = (draft: SendMessageRequest) => void;
