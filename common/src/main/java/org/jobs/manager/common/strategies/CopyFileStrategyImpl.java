package org.jobs.manager.common.strategies;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.common.entities.CopyFileTask;
import org.jobs.manager.common.shared.Task;
import org.jobs.manager.common.shared.TaskMetadata;
import org.jobs.manager.common.shared.TaskStrategy;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Component
@ToString(callSuper = true)
@Slf4j
public class CopyFileStrategyImpl implements TaskStrategy<CopyFileTask> {

    public static final String COPY_FILE_STRATEGY_CODE = "COPY_FILE";

    @Override
    public String getCode() {
        return COPY_FILE_STRATEGY_CODE;
    }

    @Override
    public String getDescription() {
        return "Copy file";
    }

    @Override
    public TaskMetadata getTaskMetadata() {
        return new TaskMetadata(getCode(), getDescription(),
                Arrays.asList(CopyFileTask.FROM_CODE,
                        CopyFileTask.TO_CODE));
    }

    @Override
    public Mono<Void> execute(Task task) {
        return Mono.empty();
    }
}
