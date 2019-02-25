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
let messages = {};

function renderMessage(message) {
    console.log("Received message", message);
    if (message.id && message.status) {
        let msgId = message.id + message.status + message.started;
        if (messages[msgId]) {
            console.log("Skip message id ", msgId);
            return;
        }

        messages[msgId] = msgId;
        let status = 'text-dark';
        if (message.status === "SUCCESS") {
            $("#jobSuccess").text(++jobSuccess);
            status = 'text-success';
        }

        if (message.status === "FAILED") {
            $("#jobFail").text(++jobFailed);
            status = 'text-danger';
        }

        //jobsContainerasdfsdf
        if (message.task && message.task.id) {
            let jobInfo = "" +
                "<div class='row'>" +
                "  <div class='col-5'>" + message.id + "</div>" +
                "  <div class='col-5'>" + message.started + "</div>" +
                "  <div class='col-2 " + status + "'>" + message.status + "</div>" +
                "</div>" +
                "";
            if (message.description) {
                jobInfo += "" +
                    "<div class='row'>" +
                    "  <div class='col-12 text-danger'>" + message.description + "</div>" +
                    "</div>" +
                    ""
            }
            $(".jobsContainer" + message.task.id).prepend(jobInfo)
        }
    }
}

$(function () {
    connect();
    $.get("/v1/jobs/listen");
});