import {Client} from "@stomp/stompjs";

const client = new Client();
client.brokerURL = 'ws://localhost:8080/ws';

client.onConnect = function (frame) {
    // client.subscribe();
    // TODO: setup the basics for subscribing to messages
}

client.onStompError = function (frame) {
    console.error(frame);
}

client.activate();