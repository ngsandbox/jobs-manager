package org.jobs.manager.common.stubs;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jobs.manager.common.shared.Task;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TestTask extends Task {

    private static final String TIMEOUT_SECS_CODE = "timeoutSecs";
    private static final String THROW_ERROR_CODE = "throwError";


    private static final long serialVersionUID = 4513958683692007945L;

    private final Integer timeoutSecs;

    private final boolean throwError;

    public static TestTask of(@NonNull Task task) {
        log.debug("Convert to test task {}", task);
        String strTimout = task.getDetails().get(TIMEOUT_SECS_CODE);
        Integer timeout = null;
        if(StringUtils.isNotEmpty(strTimout)){
            timeout = Integer.parseInt(strTimout);
        }

        return TestTask.testBuilder()
                .id(task.getId())
                .strategyCode(task.getStrategyCode())
                .timeoutSecs(timeout)
                .throwError(Boolean.valueOf(task.getDetails().get(THROW_ERROR_CODE)))
                .build();
    }

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
