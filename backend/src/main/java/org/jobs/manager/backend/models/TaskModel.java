package org.jobs.manager.backend.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jobs.manager.common.shared.Task;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Getter
@Setter
@NoArgsConstructor
@ToString
public class TaskModel implements Serializable {

    private static final long serialVersionUID = 449603100569647644L;
    /**
     * Uniquie task identifier
     *
     * @implSpec new id will be generated if this one will be empty
     */
    private String id;

    /**
     * Strategy strategyCode
     */
    @NotNull(message = "Please provide task strategy code")
    private String strategyCode;

    @Valid
    private Set<TaskDetail> details = new HashSet<>();

    private TaskModel(String id,
                      String strategyCode,
                      Set<TaskDetail> details) {
        this.id = id;
        this.strategyCode = strategyCode;
        this.details = details;
    }


    public Task takeTask() {
        log.debug("Convert from transport task {}", this);
        Map<String, String> taskDetails = new HashMap<>();
        if (details != null) {
            taskDetails = details.stream()
                    .collect(Collectors
                            .toMap(TaskDetail::getCode, TaskDetail::getValue));
        }
        String taskId = StringUtils.isEmpty(id) ? UUID.randomUUID().toString() : id;
        return new Task(taskId, strategyCode, taskDetails);
    }

    public static TaskModel toModel(Task task) {
        log.debug("Convert to transport task {}", task);
        if (task == null) {
            return null;
        }

        Set<TaskDetail> taskDetails = task.getDetails()
                .entrySet()
                .stream()
                .map(e -> new TaskDetail(e.getKey(), e.getValue()))
                .collect(Collectors.toSet());

        return new TaskModel(task.getId(),
                task.getStrategyCode(),
                taskDetails);
    }
}
