package org.jobs.manager.db.entities;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

import static javax.persistence.CascadeType.PERSIST;

@Slf4j
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class TaskDetailEntity {

    @Id
    private String id;

    private String taskId;

    private String code;

    private String value;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "taskId", cascade = PERSIST)
    private TaskEntity task;

}
