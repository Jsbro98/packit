import "./style.css";
import {createStompClient, setClientHandlers} from "./stomp";
import {chatMessageStream, inputTextBox, submitButton, usernameInput,} from "./render";
import type {Client, IFrame} from "@stomp/stompjs";
import {getMessageHistory} from "./history";
import type {Message} from "./types";

// ####
main();
// ####

function main() {
    const client: Client = createStompClient();

    setClientHandlers(client, createConnectionHandler(client));
    configSubmitButtonListener(client);

    client.activate();
}

// ---------- functions ----------

function createConnectionHandler(client: Client) {
    return async function (frame: IFrame) {
        const messageHistory: Message[] = await getMessageHistory();
        for (const message of messageHistory) {
            chatMessageStream.append(message);
        }

        client.subscribe("/topic/messages", (message) => {
            const messageRecord: Message = JSON.parse(message.body);
            chatMessageStream.append(messageRecord);
        });
    };
}

function configSubmitButtonListener(client: Client) {
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
}
