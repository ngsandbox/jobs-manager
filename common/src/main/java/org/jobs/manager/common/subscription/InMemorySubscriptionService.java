package org.jobs.manager.common.subscription;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.common.subscription.events.SubscriptionEvent;
import org.jobs.manager.common.subscription.listeners.SourceListener;
import org.jobs.manager.common.utils.CloseUtils;
import reactor.core.publisher.Flux;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class InMemorySubscriptionService implements SubscriptionService {

    private final Set<SourceListener<? super SubscriptionEvent>> listeners = ConcurrentHashMap.newKeySet();

    @Override
    public <T extends SubscriptionEvent> Flux<T> subscribe(@NonNull String sourceName) {
        log.info("Initialize new subscription from source {}", sourceName);
        return subscribe(sourceName, this::saveToListeners);
    }

    @SuppressWarnings("unchecked")
    private void saveToListeners(SourceListener tSourceListener) {
        listeners.add(tSourceListener);
    }

    @Override
    public <T extends SubscriptionEvent> void publish(@NonNull T message) {
        log.debug("Publish message to the source {}", message.getSourceName());
        listeners.stream()
                .filter(l -> l.getSourceName().equals(message.getSourceName()))
                .forEach(l -> emitMessage(message, l));
    }

    private <T extends SubscriptionEvent> void emitMessage(@NonNull T message, SourceListener<? super SubscriptionEvent> l) {
        log.debug("Publish message to the source {}", message.getSourceName());
        l.emit(message);
    }

    @Override
    public void close() {
        log.warn("Close in-memory subscription service");
        listeners.forEach(l -> CloseUtils.closeQuite(l::close));
        listeners.clear();
    }
}
