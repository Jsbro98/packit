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

/** main application function. All wiring for setup is done here */
function main() {
  // prepare the set pieces
  const renderer: Renderer = createRenderer();
  const client: Client = createStompClient();

  // create the handlers
  const connectionHandler: OnConnectHandler = createConnectionHandler(
    client,
    renderer,
  );
  const onSendHandler: OnSendHandler = createOnSendHandler(client, renderer);

  // wire everything together
  setClientHandler(client, connectionHandler);
  renderer.onSend(onSendHandler);

  // start the client side
  client.activate();
}

// ---------- functions ----------

/**
 * Takes in a STOMP client and Renderer object and creates a cllback
 * used for the connection STOMP event
 *
 * @param client - STOMP client
 * @param renderer - DOM object for UI handling
 * @returns a cutomized connection STOMP event callback used for zed connection event
 */
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
}

/**
 * Takes in a STOMP client and Renderer object and creates a callback used
 * for the send STOMP event
 *
 * @param client - STOMP client
 * @param renderer - DOM object for UI handling
 * @returns a cutomized send STOMP event callback used for zed send event
 */
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

/**
 * Retries an async operation, returning the first success.
 *
 * @param attempts - Max tries. Must be positive.
 * @param callback - Operation to run, called fresh on each try.
 * @returns The result, or the last error if every attempt failed.
 * @throws {RangeError} If `attempts` is not positive.
 *
 * @example
 * const history = await withRetries(5, getMessageHistory);
 */
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
