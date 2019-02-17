package org.jobs.manager.events;

import java.io.Serializable;

/**
 * Subscription event's wrapper to distribute objects across services and distributed components
 */
public interface TopicEvent extends Serializable {

    String getTopicName();
}
