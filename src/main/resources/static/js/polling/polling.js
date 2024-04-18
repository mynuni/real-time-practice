document.addEventListener("DOMContentLoaded", () => {

    const messageContainer = document.getElementById("message-container");
    const messageInput = document.getElementById("message");
    const sendButton = document.getElementById("send-message-button");

    const poll = () => {
        fetch("/api/messages/polling")
            .then(response => response.json())
            .then(messages => {
                messages.forEach(item => {
                    appendMessage(item.sender, item.message);
                });
            })
            .catch(error => {
                console.error("Error", error);
            });

        setTimeout(poll, 5000); // 5초 간격으로 Polling
    };

    const sendMessage = () => {
        const message = messageInput.value;
        fetch("/api/messages/polling", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({message: message})
        })
            .then(response => {
                if (response.status === 201) {
                    messageInput.value = "";
                }
            })
            .catch(error => {
                console.error("Error", error);
            });
    };

    const appendMessage = (sender, message) => {
        const listItem = document.createElement("li");
        listItem.innerText = sender + " >> " + message;
        messageContainer.appendChild(listItem);
    };

    sendButton.addEventListener("click", sendMessage);

    poll();

});