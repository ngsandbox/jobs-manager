package org.jobs.manager.backend.models;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Slf4j
@Getter
@Setter
@EqualsAndHashCode(of = "code")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TaskDetail implements Serializable {

    private static final long serialVersionUID = 8952999452630632541L;

    @NotNull(message = "The code is not privided for detail of the task")
    private String code;

    @NotNull(message = "All values should be provided for details of the task")
    private String value;
}
