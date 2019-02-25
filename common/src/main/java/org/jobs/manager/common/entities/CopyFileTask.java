package org.jobs.manager.common.entities;

import lombok.*;
import org.jobs.manager.common.shared.Task;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CopyFileTask extends Task {

    private static final long serialVersionUID = 3868972337367312647L;
    public static final String FROM_CODE = "from";
    public static final String TO_CODE = "to";

    private final String from;
    private final String to;

    @Builder(builderMethodName = "testBuilder")
    public CopyFileTask(String id,
                        String strategyCode,
                        @NonNull String from,
                        @NonNull String to) {
        super(id, strategyCode, buildDetails(from, to));
        this.from = from;
        this.to = to;
    }

    private static Map<String, String> buildDetails(String from,
                                                    String to) {
        Map<String, String> details = new HashMap<>();
        details.put(FROM_CODE, from);
        details.put(TO_CODE, to);
        return details;
    }

    public static CopyFileTask of(@NonNull Task task) {
        return new CopyFileTask(
                task.getId(),
                task.getStrategyCode(),
                task.getDetails().get(FROM_CODE),
                task.getDetails().get(TO_CODE)

        );
    }
}
