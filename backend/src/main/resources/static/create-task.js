let taskMetadata;

$(function () {
    $.get("/v1/tasks/metadata", renderTasksMetadata);
    $(".taskStrategyType").click(onClickToggleSchedulerType);
    $("#createTaskBtn").click(createTask);
    $("form").on('submit', function (e) {
        e.preventDefault();
    });

});


function onClickToggleSchedulerType(element) {
    console.log("Toggle scheduler type", element, $(this).val());
    let type = $(this).val();
    if (type === 'ON_DATE_SCHEDULER') {
        $("#cronExpression").removeClass('d-block').addClass('d-none');
        $("#onDateExpression").removeClass('d-none').addClass('d-block');
    }

    if (type === 'CRON_SCHEDULER') {
        $("#onDateExpression").removeClass('d-block').addClass('d-none');
        $("#cronExpression").removeClass('d-none').addClass('d-block');
    }
}

function createTask() {
    let properties = [];
    $("#createTaskErrors").removeClass('d-block').addClass('d-none');
    $(".createTaskProperty").each(function (index, element) {
        let input = $(element);
        properties[input.data("propName")] = input.val();
    });

    let expression = '';
    let strategyCode = $("#createTaskStrategyCode").val();
    let schedulerCode = $("input:radio[name='taskSchedulerStrategy']:checked").val();//$("#createTaskStrategyCode").val();
    if (schedulerCode === 'ON_DATE_SCHEDULER') {
        expression = $("#createTaskOnDate").val();
    }

    if (schedulerCode === 'CRON_SCHEDULER') {
        expression = $("#createTaskExpression").val();
    }

    let priority = $("#createTaskPriority").val();

    $.ajax({
        contentType: 'application/json',
        data: JSON.stringify({
            "task": {
                "strategyCode": strategyCode,
                "details": properties
            },
            "scheduler": {
                "schedulerCode": schedulerCode,
                "expression": expression,
                "priority": priority
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

