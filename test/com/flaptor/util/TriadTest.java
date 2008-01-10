package com.flaptor.util;

import junit.framework.TestCase;

public class TriadTest extends TestCase
{
    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testOrder() {
        Triad<Integer, Integer, Integer> triad = new Triad<Integer, Integer, Integer>(1,2,3);
        assertTrue(triad.first().equals(new Integer(1)));
        assertTrue(triad.second().equals(new Integer(2)));
        assertTrue(triad.third().equals(new Integer(3)));
    }

    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testEquality1() {
        assertTrue(new Triad<String, String, String>(null, null, null).equals(new Triad<String, String, String>(null, null, null)));
    }

    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testEquality2() {
        assertFalse(new Triad<String, String, String>(null, null, null).equals(new Triad<String, String, String>(null, "foo", null)));
    }
    
    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testEquality3() {
        assertTrue(new Triad<String, String, String>("can", "ti", "na").equals(new Triad<String, String, String>("can", "ti", "na")));
    }
    
    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testEquality4() {
        assertFalse(new Triad<String, String, String>(null, null, null).equals(new Triad<String, String, String>("foo", "foo", "foo")));
    }
    
    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testEquality5() {
        assertFalse(new Triad<String, String, String>(null, null, "foo").equals(new Triad<String, String, String>("foo", null, null)));
    }
}
