package org.jobs.manager.backend.models;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.common.shared.Task;

import java.util.HashMap;
import java.util.Map;


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

    private Map<String, String> details = new HashMap<>();

    private TaskModel(String id,
                      String strategyCode,
                      Map<String, String> details) {
        this.id = id;
        this.strategyCode = strategyCode;
        this.details = details;
    }


    public Task takeTask() {
        log.debug("Convert from transport task {}", this);
        return new Task(id, strategyCode, details);
    }

    public static TaskModel toModel(Task task) {
        log.debug("Convert to transport task {}", task);
        if (task == null) {
            return null;
        }

        return new TaskModel(task.getId(),
                task.getStrategyCode(),
                task.getDetails());
    }
}
