import type { Message, Renderer } from "./types";

// ---------- functions ----------

export function createRenderer(): Renderer {
  const inputTextBox = requireElement<HTMLInputElement>("#user-textbox");
  const usernameInput = requireElement<HTMLInputElement>("#username");
  const submitButton = requireElement<HTMLButtonElement>("#send-button");
  const chatBox = requireElement<HTMLDivElement>(".chat-box");

  function getDraft(): Message {
    return {
      sender: usernameInput.value.trim(),
      content: inputTextBox.value.trim(),
    };
  }

  return {
    appendToChat(message: Message) {
      appendMessage(message, chatBox);
    },

    appendHistory(messageHistory: Message[]) {
      for (const message of messageHistory) {
        appendMessage(message, chatBox);
      }
    },

    getDraft,
    clearDraft() {
      inputTextBox.value = "";
    },

    onSend(callback: (draft: Message) => void) {
      submitButton.addEventListener("click", (e) => {
        e.preventDefault();
        const draft = getDraft();
        if (!draft.sender || !draft.content) return;
        callback(draft);
      });
    },
  };
}

function createChatMessageNode(message: Message): HTMLDivElement {
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

function appendMessage(message: Message, elem: HTMLElement) {
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
