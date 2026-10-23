import "./style.css";
import { createStompClient, setClientHandler } from "./stomp";
import { createRenderer } from "./render";
import type { Client, IFrame } from "@stomp/stompjs";
import { getMessageHistory } from "./history";
import type {
  Message,
  OnConnectHandler,
  OnSendHandler,
  Renderer,
} from "./types";

// ####
main();
// ####

function main() {
  const renderer: Renderer = createRenderer();
  const client: Client = createStompClient();

  const connectionHandler: OnConnectHandler = createConnectionHandler(
    client,
    renderer,
  );
  const onSendHandler: OnSendHandler = createOnSendHandler(client, renderer);

  setClientHandler(client, connectionHandler);

  renderer.onSend(onSendHandler);

  client.activate();
}

// ---------- functions ----------

function createConnectionHandler(
  client: Client,
  renderer: Renderer,
): OnConnectHandler {
  return async function (frame: IFrame) {
    const messageHistory: Message[] = await getMessageHistory();
    renderer.appendHistory(messageHistory);

    client.subscribe("/topic/messages", (message) => {
      const messageRecord: Message = JSON.parse(message.body);
      renderer.appendToChat(messageRecord);
    });
  };
}

function createOnSendHandler(
  client: Client,
  renderer: Renderer,
): OnSendHandler {
  return function (draft: Message) {
    if (!client.connected) return; // connect guard
    client.publish({
      destination: "/app/send",
      body: JSON.stringify(draft),
    });
    renderer.clearDraft();
  };
}
