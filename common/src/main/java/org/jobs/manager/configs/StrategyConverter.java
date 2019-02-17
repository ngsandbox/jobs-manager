package org.jobs.manager.configs;

import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.entities.Task;
import org.jobs.manager.strategies.TaskStrategy;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;

@Component
@ConfigurationPropertiesBinding
@Slf4j
public class StrategyConverter implements Converter<String, TaskStrategy<? extends Task>> {
    @Override
    public TaskStrategy<? extends Task> convert(String className) {
        log.info("Get instance from class name {}", className);
        try {
            Class<?> clazz = Class.forName(className);
            Constructor<?> ctor = clazz.getConstructor();
            return (TaskStrategy<?>) ctor.newInstance(new Object[0]);
        } catch (Exception ex) {
            log.error("Could not create instance by class name {}", className, ex);
            throw new IllegalStateException("Error to initialize app configuration", ex);
        }
    }
}
