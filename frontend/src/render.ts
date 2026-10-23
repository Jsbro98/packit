import type {Message} from "./types";

export const inputTextBox = requireElement<HTMLInputElement>("#user-textbox");
export const usernameInput = requireElement<HTMLInputElement>("#username");
export const submitButton = requireElement<HTMLButtonElement>("#send-button");
export const chatBox = requireElement<HTMLDivElement>(".chat-box");

// helper for the client for creating messages
export const chatMessageStream = {
    append(message: Message) {
        const messageNode = createChatMessageNode(message);
        chatBox.appendChild(messageNode);
    },
};

// ---------- functions ----------

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

function requireElement<T extends HTMLElement>(selector: string): T {
    const element = document.querySelector<T>(selector);
    if (!element) {
        throw new Error(`Required DOM element non-existent: "${selector}"`);
    }
    return element;
}
