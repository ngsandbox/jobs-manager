package org.jobs.manager.db.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Slf4j
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "strategyCode")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class TaskDetailEntity implements Serializable {

    private static final long serialVersionUID = -6317218965632546541L;

    @Id
    @Column(name = "detailId")
    private String detailId;

    @Column(name = "taskId", insertable = false, updatable = false)
    private String taskId;

    @Column(name = "strategyCode")
    private String code;

    @Column(name = "value")
    private String value;

    static TaskDetailEntity of(String detailId, String taskId, String code, String value) {
        TaskDetailEntity entity = new TaskDetailEntity();
        entity.detailId = detailId;
        entity.taskId = taskId;
        entity.code = code;
        entity.value = value;
        return entity;
    }


}
