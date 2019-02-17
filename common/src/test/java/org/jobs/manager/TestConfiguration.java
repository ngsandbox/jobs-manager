package org.jobs.manager;

import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.configs.JobManagerProperties;
import org.jobs.manager.configs.StrategyConverter;
import org.jobs.manager.dao.JobDAO;
import org.jobs.manager.entities.Task;
import org.jobs.manager.events.TopicService;
import org.jobs.manager.strategies.TaskStrategy;
import org.jobs.manager.stubs.TestJobsDAOImpl;
import org.jobs.manager.stubs.TestTopicServiceImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Configuration
@EnableConfigurationProperties(JobManagerProperties.class)
public class TestConfiguration {

    @Inject
    public JobManagerProperties jobManagerProperties;


    @Qualifier("test")
    @Bean
    public TopicService getTopicService() {
        log.info("Inialize test topic service");
        return new TestTopicServiceImpl();
    }

    @Qualifier("test")
    @Bean
    public JobDAO getJobDAO() {
        return new TestJobsDAOImpl();
    }

    @Bean
    public List<TaskStrategy<? extends Task>> getStrategies() {
        log.info("initialize job strategies from the list {}", jobManagerProperties.getStrategies());
        StrategyConverter converter = new StrategyConverter();
        return jobManagerProperties.getStrategies().stream()
                .map(converter::convert)
                .collect(toList());
    }

    @Bean
    public JobExecutor getJobExecutor(
            @Qualifier("test") TopicService topicService,
            List<TaskStrategy<? extends Task>> strategies) {
        log.info("initialize test job executor {}", jobManagerProperties);
        return new JobExecutor(jobManagerProperties.getParalelizm(), topicService, strategies);
    }

    @Bean
    public JobService getJobService(
            JobExecutor jobExecutor,
            JobDAO jobDAO) {
        log.info("initialize job service");
        return new JobService(jobExecutor, jobDAO);
    }
}
