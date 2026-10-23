import type {Message} from "./types";

export async function getMessageHistory(): Promise<Message[]> {
    const response = await fetch("/api/history");

    if (!response.ok) {
        throw new Error(`Failed to fetch history: ${response.status}`);
    }

    return await response.json();
}
