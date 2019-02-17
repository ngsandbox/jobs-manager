package org.jobs.manager.events;

import java.util.function.Consumer;

public interface TopicService {
    <T extends TopicEvent> void subscribe(String topicName, Consumer<T> listener);

    <T extends TopicEvent> void publish(T message);
}
