import type { SendMessageRequest, Renderer, ChatMessage } from "./types";

/**
 * Creates and returns a Renderer object used as a UI layer for messaging logic
 * Gets the required elements and then configures the Renderer object using those elements
 *
 * @throws {Error} if `requireElement` fails
 * @see requireElement
 * @returns a Renderer object
 */
export function createRenderer(): Renderer {
  const inputTextBox = requireElement<HTMLInputElement>("#user-textbox");
  const usernameInput = requireElement<HTMLInputElement>("#username");
  const submitButton = requireElement<HTMLButtonElement>("#send-button");
  const chatBox = requireElement<HTMLDivElement>(".chat-box");

  function getDraft(): SendMessageRequest {
    return {
      sender: usernameInput.value.trim(),
      content: inputTextBox.value.trim(),
    };
  }

  return {
    appendToChat(message: ChatMessage) {
      appendMessage(message, chatBox);
    },

    appendHistory(messageHistory: ChatMessage[]) {
      for (const message of messageHistory) {
        appendMessage(message, chatBox);
      }
    },

    getDraft,
    clearDraft() {
      inputTextBox.value = "";
    },

    onSend(callback: (draft: SendMessageRequest) => void) {
      submitButton.addEventListener("click", (e) => {
        e.preventDefault();
        const draft = getDraft();
        if (!draft.sender || !draft.content) return;
        callback(draft);
      });
    },
  };
}

/**
 * Creates a message element for the renderer
 *
 * @param message - ChatMessage type that was sent from the server
 * @returns DOM element representing the ChatMessage that was used in the parameter
 */
function createChatMessageNode(message: ChatMessage): HTMLDivElement {
  const result: HTMLDivElement = document.createElement("div");
  result.classList.add("chat-message");

  const username: HTMLDivElement = document.createElement("div");
  username.textContent = `From: ${message.sender}`;

  const timeStamp = createTimestamp(message.timestamp);
  username.appendChild(timeStamp);

  const messageContent: HTMLParagraphElement = document.createElement("p");
  messageContent.classList.add("message-content");
  messageContent.textContent = message.content;

  result.appendChild(username);
  result.appendChild(messageContent);

  return result;
}

/**
 * Creates a message element and then appends it to the `elem`
 * parameter passed in
 *
 * @param message - ChatMessage type that was sent from the server
 * @param elem - HTMLElement that is the target of the appended message
 */
function appendMessage(message: ChatMessage, elem: HTMLElement) {
  const messageNode = createChatMessageNode(message);
  elem.appendChild(messageNode);
}

/**
 * @throws {Error} If a required element is not in the DOM.
 * @param selector - CSS selector
 * @returns the element used for the selector
 */
function requireElement<T extends HTMLElement>(selector: string): T {
  const element = document.querySelector<T>(selector);
  if (!element) {
    throw new Error(`Required DOM element non-existent: "${selector}"`);
  }
  return element;
}

/**
 * Uses a `timestamp` field from a ChatMessage object to create a span to use
 * as a timestamp element
 *
 * @param timestamp - Instant instance converted from Jackson to a string
 * @returns span element with normalized timestamp textcontent
 */
function createTimestamp(timestamp: string): HTMLSpanElement {
  const timeStamp: HTMLSpanElement = document.createElement("span");
  timeStamp.classList.add("date-time");
  const { time, date } = getDateTimeFromTimestamp(timestamp);
  timeStamp.textContent = ` | ${time} ${date}`;

  return timeStamp;
}

/**
 * Takes in a timestamp from a ChatMessage timestamp field and converts it into
 * an object with two properties: `time` and `date`
 *
 * @param timestamp - Instant instance converted from Jackson to a string
 * @returns object with `time` and `date` properties
 */
function getDateTimeFromTimestamp(timestamp: string): {
  time: string;
  date: string;
} {
  const instant = Temporal.Instant.from(timestamp);

  // show in the user's local timezone
  const local = instant.toZonedDateTimeISO(Temporal.Now.timeZoneId());

  const time = local
    .toLocaleString("en-US", { hour: "numeric", minute: "2-digit" })
    .replace(/\s/g, "")
    .toLowerCase();

  const date = local.toLocaleString("en-US", {
    month: "numeric",
    day: "numeric",
    year: "2-digit",
  });

  return { time, date };
}
