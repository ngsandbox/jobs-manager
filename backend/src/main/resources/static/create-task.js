let taskMetadata;

$(function () {
    $.get("/v1/tasks/metadata", renderTasksMetadata);
    $("#createTaskBtn").click(createTask);
    $("form").on('submit', function (e) {
        e.preventDefault();
    });

});


function createTask() {
    let properties = [];
    $("#createTaskErrors").removeClass('d-block').addClass('d-none');
    $(".createTaskProperty").each(function (index, element) {
        let input = $(element);
        properties[input.data("propName")] = input.val();
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
        success: function (data) {
            console.log("task saved", data);
            disconnect();
            location.reload();
        },
        error: function (e) {
            console.error("Error to save task ", e);
            let errorMessage = e.responseText;
            if (errorMessage) {
                try {
                    let errorDetails = JSON.parse(errorMessage);
                    if (errorDetails && errorDetails.length) {
                        errorMessage = "";
                        errorDetails.forEach(function (detail) {
                            errorMessage += detail.details + "<br />";
                        });
                    }
                } catch (ex) {
                    console.warn("Could not parse json from error details", ex);
                    errorMessage = e.responseText;
                }
            } else {
                errorMessage = e.statusText;
            }

            $("#createTaskErrors")
                .html(errorMessage)
                .removeClass('d-none')
                .addClass('d-block');
        },
        processData: false,
        type: 'POST',
        url: '/v1/tasks'
    });
}


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
                    "    <input type='text' class='form-control createTaskProperty' data-propName='" + prop + "' id='" + prop + "Value'>\n" +
                    "</div>" +
                    "")
            });
        }
    }
}

