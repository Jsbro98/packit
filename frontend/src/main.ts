import {Client} from "@stomp/stompjs";

const client = new Client();

// using '/ws/' for now to allow ngrok
client.brokerURL = '/ws';

const inputTextBox = document.querySelector('#user-textbox');
const submitButton = document.querySelector('#send-button');

submitButton.addEventListener('click', (e) => {
    e.preventDefault();
    client.publish({
        destination: "/app/send",
        body: JSON.stringify({'message': inputTextBox.value})
    })
})

client.onConnect = (frame) => {
    client.subscribe('/topic/messages', (message) => {
        document.body.append(JSON.parse(message.body).message);
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