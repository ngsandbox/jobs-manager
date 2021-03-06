package org.jobs.manager.common.entities;

import lombok.*;
import org.jobs.manager.common.shared.Task;
import org.jobs.manager.common.shared.utils.JsonHelper;

import java.io.Serializable;
import java.util.*;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class EmailTask extends Task {

    private static final long serialVersionUID = 3868972337367312647L;
    public static final String FROM_CODE = "from";
    public static final String RECIPIENTS_CODE = "recipients";
    public static final String SUBJECT_CODE = "subject";
    public static final String BODY_CODE = "body";

    private final String from;
    private final List<String> recipients;
    private final String subject;
    private final String body;

    @Builder(builderMethodName = "testBuilder")
    public EmailTask(String id,
                     String strategyCode,
                     @NonNull String from,
                     @Singular @NonNull List<String> recipients,
                     @NonNull String subject,
                     @NonNull String body) {
        super(id, strategyCode, buildDetails(from, recipients, subject, body));
        this.from = from;
        this.recipients = Collections.unmodifiableList(recipients);
        this.subject = subject;
        this.body = body;
    }

    private static Map<String, String> buildDetails(String from,
                                                    List<String> recipients,
                                                    String subject,
                                                    String body) {
        Map<String, String> details = new HashMap<>();
        details.put(FROM_CODE, from);
        details.put(RECIPIENTS_CODE, JsonHelper.toJson(new Recipients(recipients)));
        details.put(SUBJECT_CODE, subject);
        details.put(BODY_CODE, body);
        return details;
    }

    public static EmailTask of(@NonNull Task task) {
        Optional<Recipients> recipients = JsonHelper.fromJson(task.getDetails().get(RECIPIENTS_CODE), Recipients.class);
        return new EmailTask(
                task.getId(),
                task.getStrategyCode(),
                task.getDetails().get(FROM_CODE),
                recipients.map(r -> r.recipients).orElse(Collections.emptyList()),
                task.getDetails().get(SUBJECT_CODE),
                task.getDetails().get(BODY_CODE)

        );
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Recipients implements Serializable {

        private static final long serialVersionUID = -6369863528946758319L;

        private List<String> recipients;
    }
}
