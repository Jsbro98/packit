import {Client} from "@stomp/stompjs";
import './style.css';

const client = new Client();

// using '/ws/' for now to allow ngrok
client.brokerURL = '/ws';

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
client.onConnect = (frame) => {
    // TODO: add a way for this to fetch message history

    client.subscribe('/topic/messages', (message) => {
        const messageRecord = JSON.parse(message.body);
        chatBox.textContent += "User: " + messageRecord.sender + '\n';
        chatBox.textContent += "Message: " + messageRecord.message + '\n\n';
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