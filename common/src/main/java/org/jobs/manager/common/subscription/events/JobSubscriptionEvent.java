package org.jobs.manager.common.subscription.events;

import lombok.Getter;
import lombok.ToString;
import org.jobs.manager.common.entities.Job;
import org.jobs.manager.common.subscription.Topics;

import java.io.Serializable;


@Getter
@ToString
public class JobSubscriptionEvent implements SubscriptionEvent, Serializable {

    private static final long serialVersionUID = 1697757597992954891L;

    private Job job;
    private boolean last;

    public JobSubscriptionEvent(Job job, boolean last) {
        this.job = job;
        this.last = last;
    }

    @Override
    public String getSourceName() {
        return Topics.JOB_TOPIC;
    }
}