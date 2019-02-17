package org.jobs.manager.db;

import com.hazelcast.core.IMap;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.map.listener.MapListener;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.events.TopicEvent;
import org.jobs.manager.events.TopicService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Service
@Slf4j
public class HazelcastMessagingService extends HazelcastService implements TopicService {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final long reconnectIntervalMs;
    /**
     * List of listeners for the topic name
     */
    private final Set<Subscription> subscriptions = ConcurrentHashMap.newKeySet();


    public HazelcastMessagingService(@Value("org.jobs.manager.db.reconnectIntervalMs") long reconnectIntervalMs,
                                     @Value("org.jobs.manager.db.hosts") List<String> addresses) {
        super(addresses);
        this.reconnectIntervalMs = reconnectIntervalMs;
        addLifecycleListener();
    }

    @Override
    public <T extends TopicEvent> void subscribe(String topicName, Consumer<T> messageListener) {
        subscriptions.add(new Subscription(
                regId -> setupTopicLisener(topicName, messageListener, regId)
        ));
    }

    private <T extends TopicEvent> String setupTopicLisener(String topicName, Consumer<T> messageListener, String registration) {
        ITopic<T> topic = getTopic(topicName);
        if (registration != null) {
            boolean removed = topic.removeMessageListener(registration);
            log.debug("Trying to unregister listener with id {} from {}, successfully {}",
                    registration, topicName, removed);
        }

        log.debug("Add listener to the topic {}", topicName);
        return topic.addMessageListener(s -> messageListener.accept(s.getMessageObject()));
    }

    public void addMapListener(String mapName, MapListener mapListener) {
        subscriptions.add(new Subscription(
                regId -> setupMapListener(mapName, mapListener, regId)
        ));
    }

    private String setupMapListener(String mapName, MapListener mapListener, String registration) {
        IMap<Object, Object> map = getMap(mapName);
        if (registration != null) {
            boolean removed = map.removeEntryListener(registration);
            log.debug("Trying to unregister listener with id {} from {}, successfully {}",
                    registration, mapName, removed);
        }
        log.debug("Add listener to the map {}", mapName);
        return map.addEntryListener(mapListener, true);
    }

    @Override
    public <T extends TopicEvent> void publish(T message) {
        ITopic<T> topic = getTopic(message.getTopicName());
        log.trace("Publish message to the topic {}", topic.getName());

        topic.publish(message);
        log.trace("Successfully publish message to the topic {}", topic.getName());
    }

    private void addLifecycleListener() {
        getInstance().getLifecycleService().addLifecycleListener(this::handleLifecycleEvent);
    }

    @Override
    public void close() {
        log.info("Close db service");
        executorService.shutdown();
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

    @ToString
    @EqualsAndHashCode(of = "registrationId")
    private static class Subscription {

        /**
         * Current registration id
         */
        private final String registrationId;

        /**
         * Resubscribe action that receives previous registration id and returns new one
         */
        private final Function<String, String> subscribeAction;

        private Subscription resubscribe() {
            return new Subscription(subscribeAction.apply(registrationId), subscribeAction);
        }

        private Subscription(Function<String, String> subscribeAction) {
            this(subscribeAction.apply(null), subscribeAction);
        }

        private Subscription(String registrationId, Function<String, String> subscribeAction) {

            this.registrationId = registrationId;
            this.subscribeAction = subscribeAction;
        }
    }
}
