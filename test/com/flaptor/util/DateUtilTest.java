package com.flaptor.util;

import java.util.GregorianCalendar;

public class DateUtilTest extends TestCase {
    
    public void testDayPassed() {
        assertEquals(-1, DateUtil.howManyDaysPassed(new GregorianCalendar(2008,0,1), new GregorianCalendar(2007,11,31)));
        assertEquals(2, DateUtil.howManyDaysPassed(new GregorianCalendar(2008,0,1,2,2,1), new GregorianCalendar(2008,0,3,1,1,0)));
        assertEquals(31, DateUtil.howManyDaysPassed(new GregorianCalendar(2008,0,1), new GregorianCalendar(2008,1,1)));
        assertEquals(366, DateUtil.howManyDaysPassed(new GregorianCalendar(2008,0,1), new GregorianCalendar(2009,0,1)));
        assertEquals(365, DateUtil.howManyDaysPassed(new GregorianCalendar(2007,0,1), new GregorianCalendar(2008,0,1)));
        assertEquals(365*3 + 366, DateUtil.howManyDaysPassed(new GregorianCalendar(2007,0,1), new GregorianCalendar(2011,0,1)));
    }
}
