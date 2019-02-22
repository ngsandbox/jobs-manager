package org.jobs.manager.backend;

import org.jobs.manager.common.JobException;

public class BackendException extends JobException {
    public BackendException(String message) {
        super(message);
    }

    public BackendException(String message, Throwable cause) {
        super(message, cause);
    }
}
