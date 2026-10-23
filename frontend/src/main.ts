import "./style.css";
import { createStompClient, setClientHandlers } from "./stomp";
import { createRenderer } from "./render";
import type { Client, IFrame } from "@stomp/stompjs";
import { getMessageHistory } from "./history";
import type { Message, OnConnectHandler, Renderer } from "./types";

// ####
main();
// ####

function main() {
  const renderer: Renderer = createRenderer();
  const client: Client = createStompClient();

  const handler: OnConnectHandler = createConnectionHandler(client, renderer);

  setClientHandlers(client, handler);
  renderer.configSubmitListener(client);

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
