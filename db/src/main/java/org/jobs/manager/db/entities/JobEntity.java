package org.jobs.manager.db.entities;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.entities.TaskStatus;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

import static javax.persistence.CascadeType.PERSIST;

@Slf4j
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class JobEntity {
    @Id
    private String id;

    private String jobId;

    private String taskId;

    private String scheduleId;

    private TaskStatus status;

    private LocalDateTime started;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "taskId", cascade = PERSIST)
    private TaskEntity task;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "scheduleId", cascade = PERSIST)
    private ScheduleEntity schedule;

}
