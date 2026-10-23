import type { Client } from "@stomp/stompjs";
import type { Message, Renderer } from "./types";

// ---------- functions ----------

export function createRenderer(): Renderer {
  const inputTextBox = requireElement<HTMLInputElement>("#user-textbox");
  const usernameInput = requireElement<HTMLInputElement>("#username");
  const submitButton = requireElement<HTMLButtonElement>("#send-button");
  const chatBox = requireElement<HTMLDivElement>(".chat-box");

  return {
    appendToChat(message: Message) {
      appendMessage(message, chatBox);
    },

    appendHistory(messageHistory: Message[]) {
      for (const message of messageHistory) {
        appendMessage(message, chatBox);
      }
    },

    configSubmitListener(client: Client) {
      // main listener for sending messages to the server
      submitButton.addEventListener("click", (e) => {
        e.preventDefault();

        const sender = usernameInput.value.trim();
        const text = inputTextBox.value.trim();

        if (!sender || !text) return; // you need a user & message to publish

        client.publish({
          destination: "/app/send",
          body: JSON.stringify({
            sender: sender,
            content: text,
          }),
        });

        inputTextBox.value = ""; // clear the input for the next message
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
