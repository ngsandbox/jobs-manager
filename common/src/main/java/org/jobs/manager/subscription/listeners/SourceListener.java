package org.jobs.manager.subscription.listeners;

import org.jobs.manager.subscription.events.SubscriptionEvent;

public interface SourceListener<T extends SubscriptionEvent> extends AutoCloseable {

    /**
     * The source name like topic/queue or distributed maps
     */
    String getSourceName();

    /**
     * Send the event to the subscriber
     */
    void emit(T event);

    /**
     * Notifi about an error the subscriber
     */
    void error(Throwable e);

    /**
     * Close the subscription
     */
    void close();
}
