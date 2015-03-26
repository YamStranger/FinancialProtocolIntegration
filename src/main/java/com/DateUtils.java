package com;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: YAPI
 * Date: 3/25/15
 * Time: 10:43 AM
 */

/**
 * DateUtils contains functions for date conventions.
 * Main purpose - working with locales
 */
public class DateUtils {
    Logger logger = LoggerFactory.getLogger(DateUtils.class);
    public static final String dateFormat = "yyyy-MM-dd HH:mm:ss.SSS z";

    /**
     * Convent date from one locale to another
     *
     * @param sourceTimeZone      timeZone of date in dateInMilliseconds
     * @param destinationTimeZone timeZone of result date
     * @param dateInMilliseconds  milliseconds in source locale
     * @return date convented according params
     */
    public static long convent(TimeZone sourceTimeZone, TimeZone destinationTimeZone, long dateInMilliseconds) {
        //curent time
        Calendar sourceTimeZoneCalendar = Calendar.getInstance(sourceTimeZone);
        Calendar destinationTimeZoneCalendar = Calendar.getInstance(destinationTimeZone);

        Calendar gmtCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        gmtCalendar.set(Calendar.YEAR, sourceTimeZoneCalendar.get(Calendar.YEAR));
        gmtCalendar.set(Calendar.MONTH, sourceTimeZoneCalendar.get(Calendar.MONTH));
        gmtCalendar.set(Calendar.DAY_OF_MONTH, sourceTimeZoneCalendar.get(Calendar.DAY_OF_MONTH));
        gmtCalendar.set(Calendar.HOUR_OF_DAY, sourceTimeZoneCalendar.get(Calendar.HOUR_OF_DAY));
        gmtCalendar.set(Calendar.MINUTE, sourceTimeZoneCalendar.get(Calendar.MINUTE));
        gmtCalendar.set(Calendar.SECOND, sourceTimeZoneCalendar.get(Calendar.SECOND));
        gmtCalendar.set(Calendar.MILLISECOND, sourceTimeZoneCalendar.get(Calendar.MILLISECOND));
        long sourceTime = gmtCalendar.getTimeInMillis();
        gmtCalendar.set(Calendar.YEAR, destinationTimeZoneCalendar.get(Calendar.YEAR));
        gmtCalendar.set(Calendar.MONTH, destinationTimeZoneCalendar.get(Calendar.MONTH));
        gmtCalendar.set(Calendar.DAY_OF_MONTH, destinationTimeZoneCalendar.get(Calendar.DAY_OF_MONTH));
        gmtCalendar.set(Calendar.HOUR_OF_DAY, destinationTimeZoneCalendar.get(Calendar.HOUR_OF_DAY));
        gmtCalendar.set(Calendar.MINUTE, destinationTimeZoneCalendar.get(Calendar.MINUTE));
        gmtCalendar.set(Calendar.SECOND, destinationTimeZoneCalendar.get(Calendar.SECOND));
        gmtCalendar.set(Calendar.MILLISECOND, destinationTimeZoneCalendar.get(Calendar.MILLISECOND));
        long destinationTime = gmtCalendar.getTimeInMillis();
        long difference = sourceTime - destinationTime;
        return dateInMilliseconds - difference;
    }

    /**
     * Convent date from default TimeZone of this System to destination locale
     *
     * @param dateInMilliseconds
     * @return date convented according params
     */
    public static long conventCurrentToGMT(long dateInMilliseconds) {
        return convent(TimeZone.getDefault(), TimeZone.getTimeZone("GMT"), dateInMilliseconds);
    }

    public static String dateToString(long dateInMilliseconds) {
        return new SimpleDateFormat(dateFormat).format(new Date(dateInMilliseconds));
    }

    public static long stringToDate(String value) throws ParseException {
        return new SimpleDateFormat(dateFormat).parse(value).getTime();
    }
}
