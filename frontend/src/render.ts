import type { SendMessageRequest, Renderer, ChatMessage } from "./types";

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

  const messageBody: HTMLParagraphElement = document.createElement("p");
  messageBody.textContent = `${message.content}`;

  result.appendChild(username);
  result.appendChild(messageBody);

  return result;
}

/**
 * Creates a message element and then appends it to the `elem`
 * prameter passed in
 *
 * @param message - ChatMessage type that was sent from the server
 * @param elem - HTMLElement that is the target of the appended message
 */
function appendMessage(message: ChatMessage, elem: HTMLElement) {
  const messageNode = createChatMessageNode(message);
  elem.appendChild(messageNode);
}

function requireElement<T extends HTMLElement>(selector: string): T {
  const element = document.querySelector<T>(selector);
  if (!element) {
    throw new Error(`Required DOM element non-existent: "${selector}"`);
  }
  return element;
}
