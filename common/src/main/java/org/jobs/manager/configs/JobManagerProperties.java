package org.jobs.manager.configs;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Component
@ToString
@ConfigurationProperties(prefix = "org.jobs.manager")
@Validated
public class JobManagerProperties {


    /**
     * Count of threads to process tasks
     */
    private int paralelizm;

    /**
     * List of tasks' strategies
     * @see org.jobs.manager.strategies.SendEmailTaskStrategyImpl
     */
    @NotNull
    private List<String> strategies;
}
