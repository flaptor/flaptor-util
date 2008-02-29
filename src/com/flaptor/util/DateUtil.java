package com.flaptor.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Utility class for managing dates 
 * @author Martin Massera
 *
 */
public class DateUtil {
    
    public static final long MILLIS_IN_DAY = 1000 * 60 * 60 * 24;
    
    /**
     * counts how many days passed between two dates, counting days and not "24hs":
     * from 2008/1/1 13:00 to 2008/1/2 11:00 there is 1 day (even if it is only a 22 hour difference)   
     * 
     * @param from
     * @param to
     * @return 
     */
    public static int howManyDaysPassed(Calendar from, Calendar to) {
        long diff = getCanonicalDay(to).getTimeInMillis()- getCanonicalDay(from).getTimeInMillis();
        return (int)(diff / MILLIS_IN_DAY);
    }
    
    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        return 
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
            cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);        
    }
    public static Calendar getCanonicalDay(Calendar cal) {
        Calendar ret = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0 ,0 ,0);
        return ret;
    }
}
