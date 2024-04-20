document.addEventListener("DOMContentLoaded", () => {

    const messageContainer = document.getElementById("message-container");
    const messageInput = document.getElementById("message");
    const sendButton = document.getElementById("send-message-button");

    // 최초 입장 시 안 읽은 메시지를 가져옴
    const init = () => {
        fetch("/api/messages/long-polling")
            .then(response => response.json())
            .then(messages => {
                messages.forEach(message => {
                    appendMessage(message.sender, message.message);
                });
            })
            .catch(error => {
                console.error("Error", error);
            });
    }

    const longPoll = () => {
        fetch("/api/messages/long-polling/subscribe")
            .then(response => {
                if (response.status !== 200) {
                    return response.text().then(errorMessage => {
                        throw new Error(errorMessage);
                    });
                }
                return response.json();
            })
            .then(message => appendMessage(message.sender, message.message))
            .catch(error => console.error(error))
            .finally(() => longPoll()); // 정상 응답 또는 타임 아웃 등의 에러 발생 시 다시 롱 폴링
    };

    const sendMessage = () => {
        const message = messageInput.value;
        fetch("/api/messages/long-polling", {
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

    init();
    longPoll();

});