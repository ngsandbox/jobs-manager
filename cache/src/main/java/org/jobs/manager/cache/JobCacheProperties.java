package org.jobs.manager.cache;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "org.jobs.manager.cache")
public class JobCacheProperties {

    /**
     * List of distributed cache hosts
     */
    private List<String> hosts;

}
