package org.jobs.manager.db.entities;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.CascadeType.PERSIST;

@Slf4j
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@NamedEntityGraph(name = "TaskEntity.details", attributeNodes = @NamedAttributeNode("details"))
public class TaskEntity {
    @Id
    private String id;

    private String strategyCode;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "taskId", cascade = PERSIST)
    private ScheduleEntity schedule;


    @OneToMany(fetch = FetchType.EAGER, mappedBy = "taskId", cascade = PERSIST)
    private List<TaskDetailEntity> details;

}
