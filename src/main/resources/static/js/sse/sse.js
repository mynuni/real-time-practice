document.addEventListener("DOMContentLoaded", () => {

    const messageContainer = document.getElementById("message-container");

    const connect = () => {
        const eventSource = new EventSource("/api/messages/sse/connect");

        eventSource.onopen = (e) => {
            console.log("Connected", e);
            appendMessage("Connected");
        }

        eventSource.onmessage = (e) => {
            const message = JSON.parse(e.data);
            appendMessage(`SEQ=${e.lastEventId} SENDER=${message.sender} MESSAGE=${message.message}`);
        }

        eventSource.onerror = (e) => {
            console.error("Disconnected", e);
            appendMessage("Disconnected");
        }

    }

    const appendMessage = (message) => {
        const messageElement = document.createElement("li");
        messageElement.textContent = message;
        messageContainer.appendChild(messageElement);
    };

    connect();

});
