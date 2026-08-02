import {Client} from "@stomp/stompjs";
import "./style.css";

// ---------- Types ----------

interface Message {
  sender: string;
  content: string;
}

// ---------------------------

const client = new Client();

// using '/ws/' for now to allow ngrok
client.brokerURL = "/ws";

const inputTextBox = requireElement<HTMLInputElement>("#user-textbox");
const usernameInput = requireElement<HTMLInputElement>("#username");
const submitButton = requireElement<HTMLButtonElement>("#send-button");
const chatBox = requireElement<HTMLDivElement>(".chat-box");

const chatMessageStream = {
  append(message: Message) {
    const messageNode = createChatMessageNode(message);
    chatBox.appendChild(messageNode);
  },
};

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

// connection logic for listening to the server
client.onConnect = async (frame) => {
  const messageHistory: Message[] = await getMessageHistory();
  for (const message of messageHistory) {
    chatMessageStream.append(message);
  }

  client.subscribe("/topic/messages", (message) => {
    const messageRecord: Message = JSON.parse(message.body);
    chatMessageStream.append(messageRecord);
  });
};

client.onStompError = (event) => {
  console.error("Stomp error: ", event);
};

client.onWebSocketError = (event) => {
  console.error("WebSocket error:", event);
};

client.onWebSocketClose = (event) => {
  console.log("WebSocket close:", event);
};

client.activate();

// ---------- functions ----------

async function getMessageHistory(): Promise<Message[]> {
  const response = await fetch("/api/history");

  if (!response.ok) {
    throw new Error(`Failed to fetch history: ${response.status}`);
  }

  return await response.json();
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

function requireElement<T extends HTMLElement>(selector: string): T {
  const element = document.querySelector<T>(selector);
  if (!element) {
    throw new Error(`Required DOM element non-existent: "${selector}"`);
  }
  return element;
}
