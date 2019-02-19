package org.jobs.manager.db;

import org.jobs.manager.JobException;

public class DbException extends JobException {
    public DbException(String message) {
        super(message);
    }

    public DbException(String message, Throwable cause) {
        super(message, cause);
    }
}
