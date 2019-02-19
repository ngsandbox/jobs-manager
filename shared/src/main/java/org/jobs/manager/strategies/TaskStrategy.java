package org.jobs.manager.strategies;

import org.jobs.manager.entities.Task;
import reactor.core.publisher.Mono;

public interface TaskStrategy<T extends Task> {

    /**
     * Unique code of the strategy
     */
    String getCode();

    /**
     * The description of the strategy
     */
    String getDescription();

    /**
     * return Mono-wrapper with execution of the provided task
     */
    Mono<Void> execute(T task);
}
