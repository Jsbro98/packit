import { Client } from "@stomp/stompjs";
import type { OnConnectHandler } from "./types";

/**
 * Used to configure a STOMP client for STOMP operations
 *
 * @returns a STOMP client configured as needed
 */
export function createStompClient(): Client {
  const client = new Client();

  // using '/ws' for now to allow ngrok
  client.brokerURL = "/ws";

  return client;
}

/**
 * Wires logic for listening to the server to a STOMP client using a callback
 * Extra logic is used for error handling and closing of the WebSocket
 *
 * @param client - STOMP client
 * @param onConnectHandler - callback passed in to configure what to do on the connect event
 */
export function setClientHandler(
  client: Client,
  onConnectHandler: OnConnectHandler,
): void {
  client.onConnect = onConnectHandler;

  client.onStompError = (event) => {
    console.error("Stomp error: ", event);
  };

  client.onWebSocketError = (event) => {
    console.error("WebSocket error:", event);
  };

  client.onWebSocketClose = (event) => {
    console.log("WebSocket close:", event);
  };
}
