package org.jobs.manager.db.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.JobException;
import org.jobs.manager.entities.Task;
import org.jobs.manager.schedulers.Scheduler;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static javax.persistence.CascadeType.PERSIST;


@Slf4j
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class TaskEntity implements Serializable {

    private static final long serialVersionUID = 4877417877285546885L;

    @Id
    private String id;

    private String strategyCode;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "taskId", cascade = PERSIST)
    private ScheduleEntity schedule;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "taskId", cascade = PERSIST)
    private List<TaskDetailEntity> details;

    public static TaskEntity from(@NonNull Task task, @NonNull Scheduler scheduler) {
        TaskEntity document = new TaskEntity();
        document.id = task.getId();
        document.strategyCode = task.getStrategyCode();
        document.schedule = ScheduleEntity.from(scheduler);
        document.details = task.getDetails().entrySet()
                .stream()
                .map(e -> new TaskDetailEntity(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        return document;
    }

    public Tuple2<Task, Scheduler> toTask() {
        Map<String, String> docDetails = details.stream()
                .collect(Collectors.toMap(
                        TaskDetailEntity::getCode,
                        TaskDetailEntity::getValue,
                        (o, o2) -> {
                            throw new JobException("Collision detected for task " + id + " code " + o);
                        }));
        Task task = Task.builder()
                .id(id)
                .strategyCode(strategyCode)
                .details(docDetails)
                .build();

        Scheduler taskScheduler = schedule.toTaskScheduler();
        return Tuples.of(task, taskScheduler);
    }
}
