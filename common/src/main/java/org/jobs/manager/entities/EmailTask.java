package org.jobs.manager.entities;

import lombok.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jobs.manager.utils.JsonHelper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class EmailTask extends Task {

    private static final long serialVersionUID = 3868972337367312647L;
    private static final String FROM_CODE = "from";
    private static final String RECIPIENTS_CODE = "recipients";
    private static final String FROM_SUBJECT = "subject";
    private static final String FROM_BODY = "body";

    private final String from;
    private final List<String> recipients;
    private final String subject;
    private final String body;

    @Builder
    public EmailTask(String id,
                     String strategyCode,
                     @NonNull String from,
                     @NonNull List<String> recipients,
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
        details.put(FROM_SUBJECT, subject);
        details.put(FROM_BODY, body);
        return details;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Recipients {
        private List<String> recipients;
    }
}
