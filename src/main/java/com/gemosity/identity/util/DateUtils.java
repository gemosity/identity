package com.gemosity.identity.util;

import java.time.Instant;

public class DateUtils {
    public static long getTimeNow() {
        Instant instant = Instant.now();
        return instant.getEpochSecond();
    }
}
