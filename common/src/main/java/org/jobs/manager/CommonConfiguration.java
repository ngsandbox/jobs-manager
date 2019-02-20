package org.jobs.manager;

import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.configs.JobManagerProperties;
import org.jobs.manager.subscription.InMemorySubscriptionService;
import org.jobs.manager.subscription.SubscriptionService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableConfigurationProperties(JobManagerProperties.class)
@ComponentScan("org.jobs.manager")
public class CommonConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public SubscriptionService getSubscriptionService() {
        log.info("Inialize test subscription service");
        return new InMemorySubscriptionService();
    }
}
