package org.jobs.manager.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
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
     * Strategy code
     */
    private final String strategyCode;

    private final Map<String, String> details;

    @JsonCreator
    @Builder
    public Task(@NonNull String id,
                @NonNull String strategyCode,
                @NonNull Map<String, String> details) {
        this.id = id;
        this.strategyCode = strategyCode;
        this.details = details;
    }
}
