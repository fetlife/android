/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.bitlove.fetlife.nativeapp.util;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.ISODateTimeFormat;

public class DateUtil {

    private static final DateTimeFormatter parser = ISODateTimeFormat.dateTime();

    public static long parseDate(String input) {
        return parser.parseDateTime(input).getMillis();
    }

    public static long parseDate(String input, boolean local) {
        if (local) {
            return parser.parseLocalDateTime(input).toDateTime().getMillis();
        } else {
            return parser.parseDateTime(input).getMillis();
        }
    }

    public static String toString(long time) {
        return parser.print(time);
    }

    public static String toServerString(long time) {

        //2017-01-24 16:52:33.074 +0200'

        DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder().
                appendYear(4,4).
                appendLiteral('-').
                appendMonthOfYear(2).
                appendLiteral('-').
                appendDayOfMonth(2).
                appendLiteral(' ').
                appendHourOfDay(2).
                appendLiteral(':').
                appendMinuteOfHour(2).
                appendLiteral(':').
                appendSecondOfMinute(2).
                appendLiteral('.').
                appendMillisOfSecond(3).
                appendLiteral(' ').
                appendTimeZoneOffset(null,false,2,2).toFormatter();

        return dateTimeFormatter.print(time);
    }

    public static long addRoughTimeOffset(long date, double longitude) {
        long offset = (long) (longitude * 24 / 360);
        return date - (offset * 60 * 60 * 1000);
    }
}
