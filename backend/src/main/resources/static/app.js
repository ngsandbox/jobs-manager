"use strict";
let jobsListenerClient;


function initStompClient(onConnection) {
    const client = Stomp.over(new SockJS('/jobs-manager-websocket'));
    client.connect({}, function (frame) {
        console.info('Connected to the system ', frame);
        onConnection(client);
    });

    return client;
}

function connect() {
    initStompClient(function (client) {
        jobsListenerClient = client;
        jobsListenerClient.subscribe('/topic/jobs', function (pushMessage) {
            renderMessage(JSON.parse(pushMessage.body));
        });
    });
}


function disconnect(system) {
    if (jobsListenerClient) {
        console.warn("Disconnect for the system", system);
        jobsListenerClient.disconnect();
    }
}

function renderMessage(message) {
    console.log("Received message", message)
}

$(function () {
    connect();
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
});