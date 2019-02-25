package org.jobs.manager.backend.models;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString
public class ErrorDetails implements Serializable {

    private static final long serialVersionUID = 2735225803128625332L;
    private Date timestamp;
    private String message;
    private String details;

    public ErrorDetails(@NonNull Date timestamp, @NonNull String message, @NonNull String details) {
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
    }
}
