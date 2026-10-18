package com.deepconnect.ats.common.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

    public static final long MINUTES_MILLIS = 1000L * 60;
    public static final long HOUR_MILLIS = MINUTES_MILLIS * 60;
    public static final long DAY_MILLIS = HOUR_MILLIS * 24;

    /**
     *  int value ---> LocalDate (yyyy-MM-dd)
     */
    public static LocalDate formatAsLocalDate(Integer intDate) {
        if (intDate == null){
            return null;
        }
        String asString = String.valueOf(intDate);
        LocalDate date = LocalDate.parse(asString, DateTimeFormatter.BASIC_ISO_DATE);
        return date;
    }

    /**
     *  Convert LocalDate to Integer
     */
    public static Integer formatLocalDate(LocalDate date){
        if (date == null){
            return null;
        }
        DateTimeFormatter df = DateTimeFormatter.BASIC_ISO_DATE;
        String s = df.format(date);
        return Integer.valueOf(s);
    }

}
