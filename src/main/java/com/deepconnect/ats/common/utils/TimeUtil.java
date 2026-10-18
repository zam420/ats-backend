package com.deepconnect.ats.common.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/** Port of the parts of Django ats.common.utils.time_util used by the schedule jobs. */
public final class TimeUtil {

    public static final ZoneId DEFAULT_ZONE = ZoneId.of("Asia/Shanghai");
    public static final String DEFAULT_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_TIME_PATTERN);

    private TimeUtil() {
    }

    /** time_util.date_time_format: null-safe, "%Y-%m-%d %H:%M:%S". */
    public static String dateTimeFormat(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.format(DEFAULT_FORMATTER);
    }

    /** time_util.parse_date_time: blank string -> null. */
    public static LocalDateTime parseDateTime(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        return LocalDateTime.parse(text, DEFAULT_FORMATTER);
    }

    /** time_util.now (Django runs with TZ = Asia/Shanghai). */
    public static LocalDateTime now() {
        return LocalDateTime.now(DEFAULT_ZONE);
    }

    /** time_util.future_time_in_minutes */
    public static LocalDateTime futureTimeInMinutes(double minutes) {
        return now().plusNanos((long) (minutes * 60_000_000_000L));
    }

    public static LocalDateTime toDefaultZone(Instant instant) {
        return LocalDateTime.ofInstant(instant, DEFAULT_ZONE);
    }

    public static Instant toInstant(LocalDateTime dateTime) {
        return dateTime.atZone(DEFAULT_ZONE).toInstant();
    }
}
