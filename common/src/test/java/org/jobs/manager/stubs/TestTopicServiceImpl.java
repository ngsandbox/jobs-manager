package org.jobs.manager.stubs;

import org.jobs.manager.events.TopicEvent;
import org.jobs.manager.events.TopicService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
public class TestTopicServiceImpl implements TopicService {

    private List<Listener<? extends TopicEvent>> listeners = new ArrayList<>();

    @Override

    public <T extends TopicEvent> void subscribe(String topicName, Consumer<T> listener) {
        listeners.add(new Listener<>(topicName, listener));
    }

    @Override
    public <T extends TopicEvent> void publish(T message) {
        if (message != null) {
            listeners.stream()
                    .filter(s->s.topicName.equals(message.getTopicName()))
                    .forEach(s->s.publish(message));
        }
    }

    private static class Listener<T extends TopicEvent> {
        private final String topicName;
        private final Consumer<T> listener;

        public Listener(String topicName, Consumer<T> listener) {
            this.topicName = topicName;
            this.listener = listener;
        }

        @SuppressWarnings("unchecked")
        public <F extends TopicEvent> void publish(F message) {
            listener.accept((T)message);
        }
    }
}
