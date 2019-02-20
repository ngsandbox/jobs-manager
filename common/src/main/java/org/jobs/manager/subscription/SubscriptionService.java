package org.jobs.manager.subscription;

import lombok.NonNull;
import org.jobs.manager.subscription.events.SubscriptionEvent;
import org.jobs.manager.subscription.listeners.FluxSinkListener;
import org.jobs.manager.subscription.listeners.SourceListener;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.function.Consumer;

/**
 * Subscription service for connection to the topic services (RabbitMQ, Hazelcast, etc)
 */
public interface SubscriptionService extends AutoCloseable {

    /**
     * Initialize flux subscription from source provider
     */
    <T extends SubscriptionEvent> Flux<T> subscribe(String sourceName);

    /**
     * Initialize flux sink and provide a SourceListener instance for the caller
     *
     * @param sourceName A source name of the topic/queue or distributed map
     * @param consumer   Listener consumer
     */
    default <T extends SubscriptionEvent> Flux<T> subscribe(@NonNull String sourceName, @NonNull Consumer<SourceListener<T>> consumer) {
        Flux<T> flux = Flux.create(emitter ->
        {
            FluxSinkListener<T> listener = new FluxSinkListener<>(sourceName, emitter);
            consumer.accept(listener);
        }, FluxSink.OverflowStrategy.LATEST);

        ConnectableFlux<T> publish = flux.publish();
        publish.connect();
        return publish;
    }


    /**
     * Publish message to the messaging service
     */
    <T extends SubscriptionEvent> void publish(T message);

    /**
     * Release resources
     */
    void close();
}
