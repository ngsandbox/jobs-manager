package org.jobs.manager.common.shared;

import reactor.core.publisher.Mono;

public interface TaskStrategy<T extends Task> {

    /**
     * Unique strategyCode of the strategy
     */
    String getCode();

    /**
     * The description of the strategy
     */
    String getDescription();

    /**
     * Get information about task's properties and description
     * @return
     */
    TaskMetadata getTaskMetadata();

    /**
     * return Mono-wrapper with execution of the provided task
     */
    Mono<Void> execute(Task task);
}
