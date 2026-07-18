import {Client} from "@stomp/stompjs";
import './style.css';

// ---------- Types ----------

interface Message {
    sender: string;
    message: string;
}

// ---------------------------



const client = new Client();

// using '/ws/' for now to allow ngrok
client.brokerURL = '/ws';

// TODO: add null checking here for robustness
const inputTextBox = document.querySelector('#user-textbox');
const usernameInput = document.querySelector('#username');
const submitButton = document.querySelector('#send-button');
const chatBox = document.querySelector('.chat-box');

// main listener for sending messages to the server
submitButton.addEventListener('click', (e) => {
    e.preventDefault();
    const sender = usernameInput.value.trim();
    const text = inputTextBox.value.trim();
    if (!sender || !text) return; // you need a user & message to publish
    client.publish({
        destination: "/app/send",
        body: JSON.stringify({
            sender: sender,
            message: text,
        })
    })
    inputTextBox.value = ''; // clear the input for the next message
})


// connection logic for listening to the server
client.onConnect = async (frame) => {
    const messageHistory: Message[] = await getMessageHistory();
    for (const message of messageHistory) {
        displayMessage(message);
    }

    client.subscribe('/topic/messages', (message) => {
        const messageRecord: Message = JSON.parse(message.body);
        displayMessage(messageRecord);
    });
}

client.onStompError = (event) => {
    console.error("Stomp error: ", event);
}

client.onWebSocketError = (event) => {
    console.error('WebSocket error:', event);
};

client.onWebSocketClose = (event) => {
    console.log('WebSocket close:', event);
}

client.activate();


// ---------- functions ----------

async function getMessageHistory(): Promise<Message[]> {
    const response = await fetch("/api/history");

    if (!response.ok) {
        throw new Error(`Failed to fetch history: ${response.status}`);
    }

    return await response.json();
}

function displayMessage(message: Message) {
    chatBox.textContent += "User: " + message.sender + '\n';
    chatBox.textContent += "Message: " + message.message + '\n\n';
}