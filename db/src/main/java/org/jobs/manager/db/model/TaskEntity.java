package org.jobs.manager.db.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.common.JobException;
import org.jobs.manager.common.shared.Task;
import org.jobs.manager.common.schedulers.Scheduler;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Slf4j
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@NamedEntityGraphs({
        @NamedEntityGraph(name = "TaskEntity.full", attributeNodes = {@NamedAttributeNode("schedule"), @NamedAttributeNode("details")}),
        @NamedEntityGraph(name = "TaskEntity.details", attributeNodes = @NamedAttributeNode("details")),
        @NamedEntityGraph(name = "TaskEntity.schedule", attributeNodes = @NamedAttributeNode("schedule"))
})
public class TaskEntity implements Serializable {

    private static final long serialVersionUID = 4877417877285546885L;

    @Id
    @Column(name = "taskId")
    private String taskId;

    @Column(name = "strategyCode")
    private String strategyCode;

    @Column(name = "scheduleId", insertable = false, updatable = false)
    private String scheduleId;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "scheduleId")
    private ScheduleEntity schedule;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "taskId")
    private List<TaskDetailEntity> details;

    public static TaskEntity from(@NonNull Task task, @NonNull Scheduler scheduler) {
        TaskEntity document = new TaskEntity();
        document.taskId = task.getId();
        document.strategyCode = task.getStrategyCode();
        document.scheduleId = scheduler.getId();
        document.schedule = ScheduleEntity.from(task.getId(), scheduler);
        document.details = task.getDetails().entrySet()
                .stream()
                .map(e -> TaskDetailEntity.of(
                        UUID.nameUUIDFromBytes((task.getId() + e.getKey()).getBytes()).toString(), // create unique key from task id and detail's strategyCode
                        task.getId(),
                        e.getKey(),
                        e.getValue()))
                .collect(Collectors.toList());
        return document;
    }

    public Tuple2<Task, Scheduler> toTask() {
        Map<String, String> docDetails = details.stream()
                .collect(Collectors.toMap(
                        TaskDetailEntity::getCode,
                        TaskDetailEntity::getValue,
                        (o, o2) -> {
                            throw new JobException("Collision detected for task " + taskId + " strategyCode " + o);
                        }));
        Task task = Task.builder()
                .id(taskId)
                .strategyCode(strategyCode)
                .details(docDetails)
                .build();

        Scheduler taskScheduler = schedule.toTaskScheduler();
        return Tuples.of(task, taskScheduler);
    }
}
