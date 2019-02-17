package org.jobs.manager.strategies;

import org.jobs.manager.entities.Task;

public interface TaskStrategy<T extends Task> {

    /**
     * Unique code of the strategy
     */
    String getCode();

    /**
     * The description of the strategy
     */
    String getDescription();


    void execute(T task);
}
