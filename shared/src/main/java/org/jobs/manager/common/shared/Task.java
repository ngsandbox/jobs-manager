package org.jobs.manager.common.shared;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

@Slf4j
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
public class Task implements Serializable {

    private static final long serialVersionUID = -4863802330970040503L;

    /**
     * Uniquie task identifier
     */
    private final String id;

    /**
     * Strategy strategyCode
     */
    private final String strategyCode;

    /**
     * Additional details for tash execution
     */
    private final Map<String, String> details;

    @Builder
    public Task(@NonNull String id,
                @NonNull String strategyCode,
                @NonNull Map<String, String> details) {
        this.id = id;
        this.strategyCode = strategyCode;
        this.details = details;
    }
}
