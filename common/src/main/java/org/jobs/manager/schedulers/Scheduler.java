package org.jobs.manager.schedulers;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;

public interface Scheduler extends Serializable {

    /**
     * get Unique identifier
     */
    String getId();

    /**
     * Unique code of the scheduler
     */
    String getCode();

    /**
     * The String representation of the scheduler
     */
    String getExpression();

    /**
     * Get starting date time execution
     */
    LocalDateTime getStartDate();

    /**
     * Priority MAX_VALUE - highest, MIN_VALUE - lowest
     */
    int getPriority();


    /**
     * Return true if {@link #getStartDate()} is less than current time
     */
    default boolean isReady() {
        LocalDateTime now = LocalDateTime.now();
        return getStartDate().isBefore(now);
    }

    /**
     * Get next scheduler info if it support periodical execution
     */
    default Optional<Scheduler> next() {
        return Optional.empty();
    }
}
