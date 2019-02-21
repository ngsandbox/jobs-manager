package org.jobs.manager.cache.services;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import org.jobs.manager.common.subscription.listeners.SourceListener;
import org.jobs.manager.common.utils.CloseUtils;

import java.util.function.Function;

@ToString
@EqualsAndHashCode(of = {"registrationId"})
class Subscription implements AutoCloseable {

    /**
     * Current registration id
     */
    private final String registrationId;

    /**
     * The source listener instance
     */
    private final SourceListener sourceListener;

    /**
     * Resubscribe action that receives previous registration id and returns new one
     */
    private final Function<String, String> subscribeAction;

    Subscription resubscribe() {
        return new Subscription(sourceListener, subscribeAction.apply(registrationId), subscribeAction);
    }

    Subscription(@NonNull SourceListener sourceListener, Function<String, String> subscribeAction) {
        this(sourceListener, subscribeAction.apply(null), subscribeAction);
    }

    private Subscription(@NonNull SourceListener sourceListener, String registrationId, Function<String, String> subscribeAction) {
        this.sourceListener = sourceListener;
        this.registrationId = registrationId;
        this.subscribeAction = subscribeAction;
    }

    @Override
    public void close() {
        CloseUtils.closeQuite(sourceListener::close);
    }
}