package org.jobs.manager.cache.services;

import com.hazelcast.core.ITopic;
import com.hazelcast.core.LifecycleEvent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.cache.JobCacheProperties;
import org.jobs.manager.subscription.listeners.SourceListener;
import org.jobs.manager.subscription.events.SubscriptionEvent;
import org.jobs.manager.subscription.SubscriptionService;
import org.jobs.manager.utils.CloseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Service
@Slf4j
public class HazelcastMessagingService extends HazelcastService implements SubscriptionService {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final long reconnectIntervalMs;
    /**
     * List of listeners for the topic name
     */
    private final Set<Subscription> subscriptions = ConcurrentHashMap.newKeySet();

    @Autowired
    public HazelcastMessagingService(@NonNull JobCacheProperties cacheProperties) {
        super(cacheProperties.getHosts());
        this.reconnectIntervalMs = cacheProperties.getReconnectIntervalMs();
        addLifecycleListener();
    }

    @Override
    public <T extends SubscriptionEvent> Flux<T> subscribe(String sourceName) {
        return subscribe(sourceName, sourceListener ->
                subscriptions.add(
                        new Subscription(sourceListener,
                                regId -> setupTopicLisener(sourceName, sourceListener, regId)
                        )));
    }

    private <T extends SubscriptionEvent> String setupTopicLisener(String topicName, SourceListener<T> messageListener, String registration) {
        ITopic<T> topic = getTopic(topicName);
        if (registration != null) {
            log.info("Trying to unregister listener with id {} from {}",
                    registration, topicName);
            boolean removed = topic.removeMessageListener(registration);
            log.info("Unregistered listener with id {} from {}, successfully {}",
                    registration, topicName, removed);
        }

        log.debug("Add listener to the topic {}", topicName);
        return topic.addMessageListener(s -> messageListener.emit(s.getMessageObject()));
    }

    @Override
    public <T extends SubscriptionEvent> void publish(T message) {
        ITopic<T> topic = getTopic(message.getSourceName());
        log.trace("Publish message to the topic {}", topic.getName());

        topic.publish(message);
        log.trace("Successfully publish message to the topic {}", topic.getName());
    }

    private void addLifecycleListener() {
        getInstance().getLifecycleService().addLifecycleListener(this::handleLifecycleEvent);
    }

    @Override
    public void close() {
        log.info("Close cache service");
        CloseUtils.closeQuite(executorService::shutdown);
        subscriptions.forEach(s -> CloseUtils.closeQuite(s::close));
    }

    private void handleLifecycleEvent(LifecycleEvent event) {
        log.info("Hazelcast lifecycle changed. New state: {}", event.getState());
        if (event.getState() == LifecycleEvent.LifecycleState.SHUTDOWN) {
            executorService.execute(this::waitForHazelcast);
        }
    }

    private void waitForHazelcast() {
        Thread.currentThread().setName("Hazelcast-re-subscriber");
        while (!Thread.currentThread().isInterrupted()) {
            if (!this.isActive()) {
                sleep();
            } else {
                resubscribe();
                break;
            }
        }
    }

    private void resubscribe() {
        log.debug("Re-subscription for hazelcast topics started");
        addLifecycleListener();
        HashSet<Subscription> set = new HashSet<>(this.subscriptions);
        for (Subscription subscription : set) {
            this.subscriptions.remove(subscription);
            this.subscriptions.add(subscription.resubscribe());
        }
    }

    private void sleep() {
        log.debug("Waiting for hazelcast client to reconnect.");
        try {
            MILLISECONDS.sleep(reconnectIntervalMs);
        } catch (InterruptedException e) {
            log.warn("Re-subscription task was interrupted ");
            Thread.currentThread().interrupt();
        }
    }


}
