package com.trainsystem.util;

import java.time.LocalTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.trainsystem.model.Route;

public class TimeUtils {


    public static LocalTime parse(String timeStr) {
        // Remove possible "(+1d)" and trim
        String clean = timeStr.replaceAll("\\s*\\(\\+1d\\)", "").trim();
        return LocalTime.parse(clean, DateTimeFormatter.ofPattern("HH:mm"));
    }

    public static int getDurationMinutes(String departure, String arrival) {
        boolean arrivalNextDay = arrival.contains("(+1d)");

        LocalTime dep = parse(departure);
        LocalTime arr = parse(arrival);

        int minutes = (int) Duration.between(dep, arr).toMinutes();

        // Add 24h if arrival is explicitly next day or negative
        if (arrivalNextDay || minutes < 0) {
            minutes += 24 * 60;
        }

        return minutes;
    }

    // total duration from first departure to last arrival
    public static int getTripDuration(List<Route> connection) {
        if (connection == null || connection.isEmpty()) return 0;

        int totalMinutes = 0;

        for (int i = 0; i < connection.size(); i++) {
            Route leg = connection.get(i);

            // Add the duration of the leg
            totalMinutes += getDurationMinutes(leg.getDepartureTime(), leg.getArrivalTime());

            // If not the last leg, add layover to next leg
            if (i < connection.size() - 1) {
                Route nextLeg = connection.get(i + 1);
                int layover = getDurationMinutes(leg.getArrivalTime(), nextLeg.getDepartureTime());

                // If layover is negative, it means the next leg is on the next day
                if (layover < 0) layover += 24 * 60;

                totalMinutes += layover;
            }
        }

        return totalMinutes;
    }
    // Layover between two legs
public static int getLayoverMinutes(String arrivalPrev, String departureNext) {
        return getDurationMinutes(arrivalPrev, departureNext);
    }
    
    // small formatting helper for minutes 
public static String formatDuration(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;
        return hours + "h" + String.format("%02d", mins) + "m";
    }
public static void printConnection(List<Route> connection) {
    if (connection == null || connection.isEmpty()) return;

    System.out.println("Connection:");

    for (int i = 0; i < connection.size(); i++) {
        Route leg = connection.get(i);
        System.out.println("  " + leg);

        // If not the last leg, print layover to next leg
        if (i < connection.size() - 1) {
            Route nextLeg = connection.get(i + 1);
            int layover = getLayoverMinutes(leg.getArrivalTime(), nextLeg.getDepartureTime());
            if (layover < 0) layover += 24 * 60; // handle next-day departure
            System.out.printf("   ⏱ Layover in %s: %dh %02dm%n",
                    leg.getArrivalCity(),
                    layover / 60,
                    layover % 60);
        }
    }
    int totalMinutes = getTripDuration(connection); System.out.printf(">>> Total Journey Duration: %dh %02dm%n%n", totalMinutes / 60, totalMinutes % 60);
}

}
