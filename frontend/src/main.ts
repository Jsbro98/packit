import "./style.css";
import { createStompClient, setClientHandler } from "./stomp";
import { createRenderer } from "./render";
import type { Client, IFrame } from "@stomp/stompjs";
import { getMessageHistory } from "./history";
import type {
  SendMessageRequest,
  OnConnectHandler,
  OnSendHandler,
  Renderer,
  ChatMessage,
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
  return async function (_: IFrame) {
    client.subscribe("/topic/messages", (message) => {
      const messageRecord: ChatMessage = JSON.parse(message.body);
      renderer.appendToChat(messageRecord);
    });

    // FIXME: Buffer messages until messagehistory loads?

    const messageHistory = await withRetries<ChatMessage[]>(5, () => {
      return getMessageHistory();
    });

    if (Error.isError(messageHistory)) {
      return;
    }

    renderer.appendHistory(messageHistory);
  };

  // used to allow message history to have multiple attempts at fetching
  async function withRetries<T>(
    value: number,
    callback: () => Promise<T>,
  ): Promise<T | Error> {
    if (value <= 0) {
      throw new RangeError("Retry count must be positive");
    }

    let returnError: Error;

    for (let retries = 0; retries < value; retries++) {
      try {
        return await callback();
      } catch (error) {
        returnError = error as Error;
      }
    }

    return returnError!;
  }
}

function createOnSendHandler(
  client: Client,
  renderer: Renderer,
): OnSendHandler {
  return function (draft: SendMessageRequest) {
    if (!client.connected) return; // connect guard
    client.publish({
      destination: "/app/send",
      body: JSON.stringify(draft),
    });
    renderer.clearDraft();
  };
}
