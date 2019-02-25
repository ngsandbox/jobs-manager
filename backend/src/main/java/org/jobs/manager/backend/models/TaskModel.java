package org.jobs.manager.backend.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.common.shared.Task;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Getter
@Setter
@NoArgsConstructor
@ToString
public class TaskModel {

    /**
     * Uniquie task identifier
     */
    private String id;

    /**
     * Strategy strategyCode
     */
    private String strategyCode;

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
        return new Task(id, strategyCode, taskDetails);
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
