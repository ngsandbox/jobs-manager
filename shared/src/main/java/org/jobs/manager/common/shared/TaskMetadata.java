package org.jobs.manager.common.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TaskMetadata {
    /**
     * Task's strategy code
     */
    private String strategyCode;

    /**
     * Title of the tasks
     */
    private String description;

    /**
     * Codes of the additional properties of the task
     */
    private List<String> properties;
}
