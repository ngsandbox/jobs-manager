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

let jobProcessed = 0;

function renderMessage(message) {
    console.log("Received message", message)
    if (message.status && message.status === "SUCCESS") {
        $("#taskProcessed").text(++jobProcessed);
    }
}

let taskMetadata;

function renderTasksMetadata(data) {
    console.log("Task metadata", data);
    if (data && data.length) {
        taskMetadata = data.pop();
        $("#createTaskStrategyCode").val(taskMetadata.strategyCode);
        $("#createTaskDescription").text(taskMetadata.description);
        if (taskMetadata.properties) {
            taskMetadata.properties.forEach(function (prop) {
                $("#createTaskForm").append(
                    "" +
                    "<div class='form-group'>\n" +
                    "    <label for='" + prop + "Value'>" + prop + "</label>\n" +
                    "    <input type='text' class='form-control createTaskProperty' propName='" + prop + "' id='" + prop + "Value'>\n" +
                    "</div>" +
                    "")
            });
        }
    }
}

function createTask() {
    let properties = [];
    $(".createTaskProperty").each(function (index, element) {
        let input = $(element);
        properties[input.attr("propName")] = input.val();
    });

    let taskId = new Date().getMilliseconds();
    let strategyCode = $("#createTaskStrategyCode").val();
    let expression = $("#createTaskExpression").val();
    let priority = $("#createTaskPriority").val();

    $.ajax({
        contentType: 'application/json',
        data: JSON.stringify({
            "task": {
                "id": taskId,
                "strategyCode": strategyCode,
                "details": properties
            },
            "scheduler": {
                "schedulerCode": "CRON_SCHEDULER",
                "expression": expression,
                "priority": priority,
                "active": true
            }
        }),
        dataType: 'json',
        success: function (data) {
            console.log("task saved", data);
        },
        error: function (e) {
            console.error("Error to save task", e);
        },
        processData: false,
        type: 'POST',
        url: '/v1/tasks'
    });
}


$(function () {
    connect();
    $.get("/v1/jobs/listen");
    $.get("/v1/tasks");
    $.get("/v1/tasks/metadata", renderTasksMetadata);
    $("#createTaskBtn").click(createTask);
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
});