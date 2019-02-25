package org.jobs.manager.backend.models;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@EqualsAndHashCode(of = "code")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TaskDetail {
    private String code;
    private String value;
}
