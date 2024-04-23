document.addEventListener("DOMContentLoaded", () => {
    const messageContainer = document.getElementById("message-container");
    const messageInput = document.getElementById("message");
    const sendButton = document.getElementById("send-message-button");

    const stompClient = Stomp.over(new SockJS("/websocket/stomp"));
    stompClient.connect({}, (frame) => {
        console.log("Connected", frame);
        stompClient.subscribe("/sub/chat", (data) => {
            const message = JSON.parse(data.body);
            appendMessage(`${message.sender} >> ${message.message}`);
        });
    });

    const sendMessage = () => {
        stompClient.send("/pub/chat", {}, messageInput.value);
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
        stompClient.disconnect();
    }

});