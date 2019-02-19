package org.jobs.manager.db.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.io.Serializable;

import static javax.persistence.CascadeType.PERSIST;

@Slf4j
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "code")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class TaskDetailEntity implements Serializable {

    private static final long serialVersionUID = -6317218965632546541L;

    @Id
    private String id;

    private String taskId;

    private String code;

    private String value;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "taskId", cascade = PERSIST)
    private TaskEntity task;

    public TaskDetailEntity(String code, String value) {
        this.code = code;
        this.value = value;
    }


}
