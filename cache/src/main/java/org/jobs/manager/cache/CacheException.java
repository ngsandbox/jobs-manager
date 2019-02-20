package org.jobs.manager.cache;

import org.jobs.manager.JobException;

public class CacheException extends JobException {
    public CacheException(String message) {
        super(message);
    }

    public CacheException(String message, Throwable cause) {
        super(message, cause);
    }
}
