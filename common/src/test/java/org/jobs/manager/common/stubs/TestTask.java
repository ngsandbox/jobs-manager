package org.jobs.manager.common.stubs;

import lombok.*;
import org.jobs.manager.entities.Task;

import java.util.HashMap;
import java.util.Map;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TestTask extends Task {

    private static final String TIMEOUT_SECS_CODE = "timeoutSecs";
    private static final String THROW_ERROR_CODE = "throwError";


    private static final long serialVersionUID = 4513958683692007945L;

    private final Integer timeoutSecs;

    private final boolean throwError;

    @Builder(builderMethodName = "testBuilder")
    public TestTask(@NonNull String id,
                    @NonNull String strategyCode,
                    Integer timeoutSecs,
                    boolean throwError) {
        super(id, strategyCode, buildDetails(timeoutSecs, throwError));
        this.timeoutSecs = timeoutSecs;
        this.throwError = throwError;
    }

    private static Map<String, String> buildDetails(Integer timeoutSecs,
                                                    boolean throwError) {
        Map<String, String> details = new HashMap<>();
        if (timeoutSecs != null) {
            details.put(TIMEOUT_SECS_CODE, Integer.toString(timeoutSecs));
        }

        details.put(THROW_ERROR_CODE, Boolean.toString(throwError));
        return details;
    }
}
