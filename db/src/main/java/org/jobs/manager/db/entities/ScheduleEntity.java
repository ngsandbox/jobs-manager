package org.jobs.manager.db.entities;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

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
public class ScheduleEntity {
    @Id
    private String id;

    private String expression;

    private LocalDateTime startDate;

    private String taskId;

    private int priority;

    private boolean active;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "taskId", cascade = PERSIST)
    private TaskEntity task;
}
