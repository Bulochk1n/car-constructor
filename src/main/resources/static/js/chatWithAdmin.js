var stompClient = null;

function connect() {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/admin-replies', function (message) {
            showMessage(JSON.parse(message.body));
        });
    });
}

function showMessage(message) {
    var messageArea = document.querySelector('.chat-container');
    var messageElement = document.createElement('div');
    messageElement.className = "alert alert-info mt-2 car-container";

    var senderElement = document.createElement('h3');
    senderElement.textContent = message.sender;

    var contentElement = document.createElement('p');
    contentElement.textContent = message.content;

    messageElement.appendChild(senderElement);
    messageElement.appendChild(contentElement);
    messageArea.appendChild(messageElement);

    // Скролл вниз после добавления нового сообщения
    messageArea.scrollTop = messageArea.scrollHeight;
}

document.addEventListener('DOMContentLoaded', function () {
    connect();
});
