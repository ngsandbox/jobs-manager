package org.jobs.manager.db.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;

import static javax.persistence.CascadeType.PERSIST;

@Slf4j
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "code")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "detail")
public class TaskDetailEntity implements Serializable {

    private static final long serialVersionUID = -6317218965632546541L;

    @Id
    @Column(name = "detailId")
    private String detailId;

    @Column(name = "taskId")
    private String taskId;

    @Column(name = "code")
    private String code;

    @Column(name = "value")
    private String value;

//    @ManyToOne(fetch = FetchType.LAZY, cascade = PERSIST)
//    private TaskEntity task;

    static TaskDetailEntity of(String detailId, String taskId, String code, String value) {
        TaskDetailEntity entity = new TaskDetailEntity();
        entity.detailId = detailId;
        entity.taskId = taskId;
        entity.code = code;
        entity.value = value;
        return entity;
    }


}
