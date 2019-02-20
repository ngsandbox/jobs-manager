package org.jobs.manager.db.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.GenericGenerator;
import org.jobs.manager.entities.Job;
import org.jobs.manager.entities.Task;
import org.jobs.manager.entities.TaskStatus;
import org.jobs.manager.schedulers.Scheduler;
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
@Table(name = "history")
public class JobHistoryEntity implements Serializable {

    private static final long serialVersionUID = -9124289507532315747L;

    /**
     * History of the job identifier
     */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String historyId;

    private String jobId;

//    @Column(name="taskId", insertable = false, updatable = false)
//    private String taskId;

    private String scheduleId;

    private TaskStatus status;

    private LocalDateTime started;

    private String description;

    @ManyToOne
//    @JoinColumn(name = "taskId")
    private TaskEntity task;

    public static Job<Task> queued(@NonNull Task task, @NonNull Scheduler scheduler) {
        log.trace("Transform task's entities and create queued job. Task: {}, scheduler: {}", task, scheduler);
        return Job.queued(task, scheduler);
    }


    public static JobHistoryEntity from(@NonNull Job<Task> job) {
        log.trace("Transform job to entity. {}", job);
        JobHistoryEntity entity = new JobHistoryEntity();
        entity.setJobId(job.getId());
//        entity.setTaskId(job.getTask().getId());
        entity.setScheduleId(job.getScheduler().getId());
        entity.setStatus(TaskStatus.QUEUED);
        entity.setStarted(job.getStarted());
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
