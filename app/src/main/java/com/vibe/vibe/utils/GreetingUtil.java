package com.vibe.vibe.utils;

import java.util.Calendar;

public class GreetingUtil {

    public static String getGreeting() {
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        if (hourOfDay >= 0 && hourOfDay < 12) {
            return "Good Morning";
        } else if (hourOfDay >= 12 && hourOfDay < 18) {
            return "Good Afternoon";
        } else {
            return "Good Evening";
        }
    }
}
