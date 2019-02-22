package org.jobs.manager.common;

import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.common.dao.JobDAO;
import org.jobs.manager.common.stubs.TestJobsDAOImpl;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableAutoConfiguration
public class TestConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public JobDAO getJobDAO(){
        return new TestJobsDAOImpl();
    }
}
