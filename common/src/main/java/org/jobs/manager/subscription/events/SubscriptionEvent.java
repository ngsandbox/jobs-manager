package org.jobs.manager.subscription.events;

import java.io.Serializable;

/**
 * Subscription event's wrapper to distribute objects across services and distributed components
 */
public interface SubscriptionEvent extends Serializable {

    /**
     * Source name
     */
    String getSourceName();

    /**
     * Notification, that this is the last message
     */
    boolean isLast();
}
