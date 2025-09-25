package com.railwaysearch.util;

import java.time.LocalTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

public class TimeUtils {

    public static LocalTime parse(String timeStr) {
        return LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
    }

    public static int getDurationMinutes(String departure, String arrival) {
        LocalTime dep = parse(departure);
        LocalTime arr = parse(arrival);
        return (int) Duration.between(dep, arr).toMinutes();
    }
}
