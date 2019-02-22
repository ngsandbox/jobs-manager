package org.jobs.manager.db.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.GenericGenerator;
import org.jobs.manager.common.entities.Job;
import org.jobs.manager.common.shared.Task;
import org.jobs.manager.common.entities.TaskStatus;
import org.jobs.manager.common.schedulers.Scheduler;
import reactor.util.function.Tuple2;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;


@Slf4j
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@NamedEntityGraph(name = "JobHistoryEntity.task", attributeNodes = @NamedAttributeNode("task"))
@NamedEntityGraphs({
        @NamedEntityGraph(name = "JobHistoryEntity.full", attributeNodes = {
                @NamedAttributeNode(value = "task", subgraph = "task"),
        }, subgraphs = {
                @NamedSubgraph(name = "task", attributeNodes = {@NamedAttributeNode("schedule"), @NamedAttributeNode("details")})
        })
})
public class JobHistoryEntity implements Serializable {

    private static final long serialVersionUID = -9124289507532315747L;

    /**
     * History of the job identifier
     */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "historyId")
    private String historyId;

    @Column(name = "jobId")
    private String jobId;

    @Column(name = "taskId", insertable = false, updatable = false)
    private String taskId;

    @Column(name = "status")
    private TaskStatus status;

    @Column(name = "started")
    private LocalDateTime started;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "taskId")
    private TaskEntity task;

    public static Job<Task> queued(@NonNull Task task, @NonNull Scheduler scheduler) {
        log.trace("Transform task's entities and create queued job. Task: {}, scheduler: {}", task, scheduler);
        return Job.queued(task, scheduler);
    }


    public static JobHistoryEntity from(@NonNull Job<Task> job) {
        log.trace("Transform job to entity. {}", job);
        JobHistoryEntity entity = new JobHistoryEntity();
        entity.jobId = job.getId();
        entity.taskId = job.getTask().getId();
        entity.task = TaskEntity.from(job.getTask(), job.getScheduler());
        entity.status = job.getStatus();
        entity.started = job.getStarted();
        entity.description = job.getDescription();
        return entity;
    }

    public Job<Task> toJob() {
        log.trace("Convert job entity id {}", historyId);
        Objects.requireNonNull(task, "Task instance could not be empty for id " + historyId);
        Tuple2<Task, Scheduler> taskInfo = task.toTask();
        return Job.toJob(jobId,
                started,
                taskInfo.getT1(),
                taskInfo.getT2(),
                status,
                description
        );
    }
}
