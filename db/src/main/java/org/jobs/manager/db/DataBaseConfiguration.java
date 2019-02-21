package org.jobs.manager.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@EnableJpaRepositories
@EnableTransactionManagement
@EntityScan
@Configuration
@ComponentScan("org.jobs.manager.db")
public class DataBaseConfiguration {
}
