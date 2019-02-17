package org.jobs.manager.db;

import org.jobs.manager.JobException;

public class CacheException extends JobException {
    public CacheException(String message) {
        super(message);
    }

    public CacheException(String message, Throwable cause) {
        super(message, cause);
    }
}
