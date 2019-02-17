package org.jobs.manager.utils;

import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public final class DateUtils {

    public static LocalDateTime convertTo(@Nullable Date date) {
        if (date == null)
            return null;
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static Date convertTo(@Nullable LocalDateTime dateTime) {
        if (dateTime == null)
            return null;
        return Date
                .from(dateTime.atZone(ZoneId.systemDefault())
                        .toInstant());
    }
}
