package org.spring.jobs.manager.schedulers;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;

public interface JobSchedule extends Serializable {

    /**
     * Get starting date time execution
     */
    LocalDateTime getStartDate();

    /**
     * Return true if {@link #getStartDate()} is less than current time
     */
    default boolean isReady() {
        LocalDateTime now = LocalDateTime.now();
        return getStartDate().isBefore(now);
    }

    /**
     * Get next schedule info if it support periodical execution
     */
    default Optional<JobSchedule> next() {
        return Optional.empty();
    }
}
