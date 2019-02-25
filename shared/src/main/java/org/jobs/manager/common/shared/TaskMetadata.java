package org.jobs.manager.common.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TaskMetadata {
    private String strategyCode;
    private String description;
    private List<String> properties;
}
