package org.jobs.manager.db;

import com.hazelcast.config.Config;
import com.hazelcast.core.*;
import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.db.util.HazelcastConfig;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
public class HazelcastService {
    private final int RECONNECT_TIMEOUT = 2;
    private volatile HazelcastInstance instance;
    private final Config config;

    HazelcastService(List<String> hosts) {
        this.config = HazelcastConfig.serverConfig(hosts);
    }

    HazelcastInstance getInstance() {
        log.trace("Get hazelcast instance ");
        HazelcastInstance localInstance = instance;
        if (localInstance == null) {
            synchronized (HazelcastInstance.class) {
                log.info("Create hazelcast instance");
                localInstance = instance;
                if (localInstance == null) {
                    if (config == null) {
                        throw new CacheException("Uninitialized Hazelcast configuration!");
                    }
                    instance = localInstance = Hazelcast.newHazelcastInstance(config);
                    log.info("Hazelcast instance created");
                }
            }
        }
        return localInstance;
    }

    /**
     * Check that the connection still alive
     */
    boolean isActive() {
        try {
            getInstance().getName();
            return true;
        } catch (IllegalStateException inactiveException) {
            log.error("Inactive hazelcast client!", inactiveException);
            return reconsume(true, (val) -> val);
        }
    }

    <K, V> IMap<K, V> getMap(String mapName) {
        log.trace("Get map by name {}", mapName);
        try {
            return getInstance().getMap(mapName);
        } catch (IllegalStateException inactiveException) {
            log.error("Inactive hazelcast client!", inactiveException);
            return reconsume(mapName, (name) -> getInstance().getMap(name));
        }
    }

    <K> ISet<K> getSet(String setName) {
        log.debug("Get set by name {}", setName);
        try {
            return getInstance().getSet(setName);
        } catch (IllegalStateException inactiveException) {
            log.error("Inactive hazelcast client!", inactiveException);
            return reconsume(setName, (name) -> getInstance().getSet(name));
        }
    }

    private <P, T> T reconsume(P param, Function<P, T> provider) {
        try {
            instance = null;
            return provider.apply(param);
        } catch (IllegalStateException ex) {
            logWait(ex);
            throw new CacheException("Could not reconnect to the hazelcast service", ex);
        }
    }

    <EVENT> ITopic<EVENT> getTopic(String topicName) {
        log.debug("Get topic by name {}", topicName);
        try {
            return getInstance().getTopic(topicName);
        } catch (IllegalStateException inactiveException) {
            log.error("Inactive hazelcast client! Trying to reconnect after 2 sec", inactiveException);
            return reconsume(topicName, (name) -> getInstance().getTopic(name));
        }
    }

    private void logWait(IllegalStateException ex) {
        log.error("Inactive hazelcast client! Waiting 2 sec and rethrowing exception", ex);
        try {
            TimeUnit.SECONDS.sleep(RECONNECT_TIMEOUT);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("logWait was interrupted!", e);
        }
    }

    public void close() {
        HazelcastInstance lclInstance = instance;
        if (lclInstance != null) {
            log.debug("Shutdown hazelcast instance");
            lclInstance.shutdown();
        }
    }
}
