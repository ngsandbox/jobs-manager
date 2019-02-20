package org.jobs.manager.cache;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "org.jobs.manager.cache")
public class JobCacheProperties {

    /**
     * The timeout in milliseconds for reconnection cache client to the cluster again
     */
    private int reconnectIntervalMs;

    /**
     * List of distributed cache hosts
     */
    private List<String> hosts;

}
