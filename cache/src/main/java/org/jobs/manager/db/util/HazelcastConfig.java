package org.jobs.manager.db.util;

import com.hazelcast.config.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class HazelcastConfig {

    private static final String MERGE_POLICY = "com.hazelcast.map.merge.PutIfAbsentMapMergePolicy";
    private static final String MAP_STORE_FACTORY = "ru.sberbank.fxp.hazelcast.factories.StoreFactory";
    private static final String ZOOKEEPER_URL_PROPERTY = "zookeeper_url";
    private static final String HAZELCAST_DISCOVERY = "hazelcast.discovery.enabled";


    private HazelcastConfig() {
        log.info("Init hazelcast config");
    }

    public static Config serverConfig(List<String> hosts) {
        return new HazelcastConfig().createServerConfig(hosts);
    }

    private Config createServerConfig(List<String> hosts) {
        log.info("Create hazelcast config");
        Config config = new Config();
        config.setInstanceName("JOB_MANAGER_CACHE");

        config.addTopicConfig(createTopicConfig("JOB_TOPIC"));

        config.setNetworkConfig(createNetworkConfig(hosts));
        config.setProperty(HAZELCAST_DISCOVERY, "true");
        config.setProperty("hazelcast.logging.type", "slf4j");
        return config;
    }

    private MapConfig createInMemoryMap(String name, String nearCacheName, int timeToLive, int size) {
        log.info("Create inmemory map {} with nearcache {} and TTL {} and eviction size {}", name, nearCacheName, timeToLive, size);
        MapConfig mapConfig = new MapConfig(name);
        NearCacheConfig nearCache = new NearCacheConfig(nearCacheName);
        EvictionConfig evictionConfig = new EvictionConfig().setSize(size);
        nearCache.setEvictionConfig(evictionConfig);

        return mapConfig
                .setTimeToLiveSeconds(timeToLive)
                .setMaxIdleSeconds(0)
                .setEvictionPolicy(EvictionPolicy.LRU)
                .setBackupCount(1)
                .setMergePolicy(MERGE_POLICY)
                .setStatisticsEnabled(true);
        //.setNearCacheConfig(createNearCache(nearCacheName, 100));
    }

    private MapConfig createPersistedInMemoryMap(String name) {
        log.info("Create persisted inmemory map {}", name);
        MapConfig mapConfig = new MapConfig(name);
        return mapConfig
                .setTimeToLiveSeconds(0)
                .setMaxIdleSeconds(0)
                .setEvictionPolicy(EvictionPolicy.LRU)
                .setBackupCount(1)
                .setMergePolicyConfig(new MergePolicyConfig())//PutIfAbsentMapMergePolicy
                .setStatisticsEnabled(true)
                .setMapStoreConfig(createMapStore());
    }

    private NetworkConfig createNetworkConfig(List<String> hosts) {
        NetworkConfig networkConfig = new NetworkConfig();
        JoinConfig joinConfig = new JoinConfig();
        joinConfig.setAwsConfig(new AwsConfig().setEnabled(false));
        joinConfig.setMulticastConfig(new MulticastConfig().setEnabled(false));
        TcpIpConfig tcpIpConfig = new TcpIpConfig();
        hosts.forEach(tcpIpConfig::addMember);
        joinConfig.setTcpIpConfig(tcpIpConfig.setEnabled(true));
        networkConfig.setJoin(joinConfig);
        return networkConfig;
    }


    private TopicConfig createTopicConfig(String name) {
        return new TopicConfig(name);
    }

    private MapStoreConfig createMapStore() {
        MapStoreConfig mapStore = new MapStoreConfig();
        return mapStore
                .setInitialLoadMode(MapStoreConfig.InitialLoadMode.EAGER)
                .setFactoryClassName(MAP_STORE_FACTORY)
                .setWriteCoalescing(false);
    }
}
