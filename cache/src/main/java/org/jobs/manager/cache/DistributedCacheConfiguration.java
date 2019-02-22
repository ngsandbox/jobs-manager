package org.jobs.manager.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Slf4j
@EnableConfigurationProperties(JobCacheProperties.class)
@ComponentScan
@Configuration
public class DistributedCacheConfiguration {
}
