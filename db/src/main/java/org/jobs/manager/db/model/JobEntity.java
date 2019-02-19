package org.jobs.manager.db.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.entities.TaskStatus;
import org.springframework.data.annotation.Id;

import javax.persistence.Entity;
import java.io.Serializable;
import java.time.LocalDateTime;


@Slf4j
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class JobEntity implements Serializable {

    private static final long serialVersionUID = -9124289507532315747L;

    @Id
    private String id;

    private String jobId;

    private String taskId;

    private String scheduleId;

    private TaskStatus status;

    private LocalDateTime started;
}
