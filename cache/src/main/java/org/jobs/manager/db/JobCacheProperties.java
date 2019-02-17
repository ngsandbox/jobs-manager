package org.jobs.manager.db;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "org.jobs.manager.db")
public class JobCacheProperties {

    private int reconnectIntervalMs;

    private List<String> hosts;

}
