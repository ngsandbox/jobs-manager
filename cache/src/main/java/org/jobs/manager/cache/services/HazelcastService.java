package org.jobs.manager.cache.services;

import com.hazelcast.config.Config;
import com.hazelcast.core.*;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.cache.util.HazelcastConfig;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class HazelcastService {
    private final AtomicBoolean active = new AtomicBoolean(true);
    private final HazelcastInstance instance;

    HazelcastService(@NonNull List<String> hosts) {
        log.info("Initialize hazelcast configuration with hosts {}", hosts);
        Config config = HazelcastConfig.serverConfig(hosts);
        this.instance = Hazelcast.newHazelcastInstance(config);
    }

    protected final HazelcastInstance getInstance() {
        return instance;
    }

    <K, V> IMap<K, V> getMap(String mapName) {
        return getInstance().getMap(mapName);
    }

    <K> ISet<K> getSet(String setName) {
        log.debug("Get set by name {}", setName);
        return getInstance().getSet(setName);
    }

    <EVENT> ITopic<EVENT> getTopic(String topicName) {
        log.debug("Get topic by name {}", topicName);
        return getInstance().getTopic(topicName);
    }

    public void close() {
        if (active.get()) {
            log.debug("Shutdown hazelcast instance");
            active.set(false);
            instance.shutdown();
        }
    }
}
