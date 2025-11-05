package com.trainsystem.util;

import java.time.DayOfWeek;
import java.util.EnumSet;
import java.util.Set;

public class DayUtils {

    public static DayOfWeek singleDay(String dayString) {
        switch (dayString.toLowerCase()) {
            case "monday":
             case "mon":
                return DayOfWeek.MONDAY;

            case "tuesday":
            case"tues":
                return DayOfWeek.TUESDAY;

            case "wednesday":
            case "wed":
                return DayOfWeek.WEDNESDAY;

            case "thursday":
            case "thurs":
                return DayOfWeek.THURSDAY;

            case "friday":
            case  "fri":
                return DayOfWeek.FRIDAY;

            case "saturday":
            case "sat":
                return DayOfWeek.SATURDAY;

            case "sunday":
            case "sun":
                return DayOfWeek.SUNDAY;

            default:
                return null;
        }
    }

    public static Set<DayOfWeek> parseDash(String dayString) {
        Set<DayOfWeek> days = EnumSet.noneOf(DayOfWeek.class);
        String[] indiv = dayString.split("-");
        if (indiv.length != 2) {
            return days;
        }

        DayOfWeek start = singleDay(indiv[0].trim());
        DayOfWeek end = singleDay(indiv[1].trim());

        if (start == null || end == null) {
            return days;
        }

        int starting_no = start.ordinal();
        int ending_no = end.ordinal();

        for (int i = 0; i < 7; i++) {
            int j = (starting_no + i) % 7;
            days.add(DayOfWeek.of(j + 1));
            if (j == ending_no)
                break;
        }
        return days;

    }

    public static Set<DayOfWeek> multipleDay(String dayString) {

        Set<DayOfWeek> days = EnumSet.noneOf(DayOfWeek.class);

        if (dayString == null || dayString.trim().isEmpty()) {
            return days;
        }

        dayString = dayString.trim();

        // if daily
        if (dayString.equalsIgnoreCase("Daily")) 
        {
            return EnumSet.allOf(DayOfWeek.class);
        }

        // if - Tue-Thu
        if (dayString.contains("-"))
        {
            days.addAll(parseDash(dayString));
        }

        // Wed,Thurs
       else if(dayString.contains(",") && !dayString.contains("-"))
        {
            for(String week: dayString.split(","))
            {
                DayOfWeek day = singleDay(week.trim());
                if(day != null)
                {
                    days.add(day);
                }
            }
        }
        
        //tuesday
        else if(!dayString.contains("-") && !dayString.contains(","))
        {
            DayOfWeek day = singleDay(dayString);
            if(day != null)
            {
                days.add(day);
            }
        }

    return days;

    }
}
