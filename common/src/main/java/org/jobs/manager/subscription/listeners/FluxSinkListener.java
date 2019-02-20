package org.jobs.manager.subscription.listeners;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.subscription.events.SubscriptionEvent;
import reactor.core.publisher.FluxSink;

@Slf4j
@Getter
public class FluxSinkListener<T extends SubscriptionEvent> implements SourceListener<T> {

    private final FluxSink<T> emitter;
    private final String sourceName;

    public FluxSinkListener(@NonNull String sourceName, @NonNull FluxSink<T> emitter) {
        log.debug("Register flux listener for source {}", sourceName);
        this.sourceName = sourceName;
        this.emitter = emitter;
    }

    @Override
    public void emit(T event) {
        log.trace("Emit event for source {}. {}", sourceName, event);
        emitter.next(event);
        if (event.isLast()) {
            log.info("Complete emitter for source {}", sourceName);
            emitter.complete();
        }
    }

    @Override
    public void error(Throwable e) {
        log.warn("The error catched for source {}", sourceName, e);
        emitter.error(e);
    }

    @Override
    public void close() {
        log.warn("Complete emitter for source {}", sourceName);
        emitter.complete();
    }
}
