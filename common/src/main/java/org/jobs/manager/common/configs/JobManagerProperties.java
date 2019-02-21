package org.jobs.manager.common.configs;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Component
@ToString
@ConfigurationProperties(prefix = "org.jobs.manager")
@Validated
public class JobManagerProperties {

    /**
     * Count of threads to run the jobs
     */
    private int paralelizm;

    /**
     * Count of available slots to process tasks
     *
     * @implNote Accoring to that not all operations could be non-blocking, like RDBMS call, the slots count has to be near the count of the {@link #paralelizm}
     */
    private int slots;
}
