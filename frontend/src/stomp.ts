import { Client, type IFrame } from "@stomp/stompjs";

export function createStompClient(): Client {
  const client = new Client();

  // using '/ws/' for now to allow ngrok
  client.brokerURL = "/ws";

  return client;
}

// connection logic for listening to the server
export function setClientHandler(
  client: Client,
  onConnectHandler: (frame: IFrame) => Promise<void>,
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
