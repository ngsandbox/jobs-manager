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

function disconnect() {
    if (jobsListenerClient) {
        console.warn("Disconnect for the system");
        jobsListenerClient.disconnect();
    }
}

function connect() {
    initStompClient(function (client) {
        jobsListenerClient = client;
        jobsListenerClient.subscribe('/topic/jobs', function (pushMessage) {
            renderMessage(JSON.parse(pushMessage.body));
        });
    });
}


let jobSuccess = 0;
let jobFailed = 0;

function renderMessage(message) {
    console.log("Received message", message)
    if (message.status && message.status === "SUCCESS") {
        if (message.status === "SUCCESS") {
            $("#jobSuccess").text(++jobSuccess);
        }

        if (message.status === "FAILED") {
            $("#jobFail").text(++jobFailed);
        }
    }
}

$(function () {
    connect();
    $.get("/v1/jobs/listen");
});