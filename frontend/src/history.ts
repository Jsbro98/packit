import type { ChatMessage } from "./types";

/**
 * Fetches the current chat history from the server using the '/api/history' path
 *
 * @returns a Promise with an array for the current chat history
 */
export async function getMessageHistory(): Promise<ChatMessage[]> {
  const response = await fetch("/api/history");

  if (!response.ok) {
    throw new Error(`Failed to fetch history: ${response.status}`);
  }

  return await response.json();
}
