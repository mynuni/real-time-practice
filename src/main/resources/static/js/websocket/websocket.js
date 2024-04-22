document.addEventListener("DOMContentLoaded", () => {
    const messageContainer = document.getElementById("message-container");
    const messageInput = document.getElementById("message");
    const sendButton = document.getElementById("send-message-button");

    const webSocket = new WebSocket("ws://localhost:8080/chat");
    webSocket.onopen = (e) => {
        console.log("Connected", e);
        appendMessage("Connected");
    }

    webSocket.onmessage = (e) => {
        const message = JSON.parse(e.data);
        appendMessage(`${message.sender} >> ${message.message}`);
    }

    webSocket.onerror = (e) => {
        console.error("Disconnected", e);
        appendMessage("Disconnected");
    }

    const sendMessage = () => {
        webSocket.send(messageInput.value);
        messageInput.value = "";
    }

    const appendMessage = (message) => {
        const messageElement = document.createElement("li");
        messageElement.textContent = message;
        messageContainer.appendChild(messageElement);
    }

    sendButton.addEventListener("click", sendMessage);

    // 언로드 시 연결 해제
    window.onbeforeunload = () => {
        console.log("Disconnected by unload");
        webSocket.close();
    }

});
