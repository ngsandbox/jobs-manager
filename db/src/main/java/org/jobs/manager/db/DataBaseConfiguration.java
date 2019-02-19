package org.jobs.manager.db;

import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.dao.JobDAO;
import org.jobs.manager.db.repositories.JobRepository;
import org.jobs.manager.db.repositories.TaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
//@EnableAutoConfiguration
@Configuration
//@ComponentScan("org.jobs.manager.db")
//@EnableReactiveMongoRepositories(basePackageClasses = {JobRepository.class, TaskRepository.class})
//@EnableReactive
//@EnableMongoRepositories
public class DataBaseConfiguration {

//    @Bean
//    public MongoTransactionManager transactionManager(MongoDbFactory dbFactory) {
//        return new MongoTransactionManager(dbFactory);
//    }

//    @Primary
//    @Bean
//    @Override
//    public MongoClient reactiveMongoClient() {
//        MongoClient client = MongoClients.create();
//        return client;
//    }

    @Bean
    public JobDAO jobDAO(JobRepository jobRepository,
                         TaskRepository taskRepository) {
        return new DatabaseJobDAOImpl(jobRepository, taskRepository);
    }
}
