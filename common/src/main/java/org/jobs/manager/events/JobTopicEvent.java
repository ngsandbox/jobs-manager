package org.jobs.manager.events;

import lombok.Getter;
import lombok.ToString;
import org.jobs.manager.entities.Job;

import java.io.Serializable;


@Getter
@ToString
public class JobTopicEvent implements TopicEvent, Serializable {

    private static final long serialVersionUID = 1697757597992954891L;

    private Job job;

    public JobTopicEvent(Job job) {
        this.job = job;
    }

    @Override
    public String getTopicName() {
        return Topics.JOB_TOPIC;
    }
}