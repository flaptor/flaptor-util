package com.flaptor.util;


public class PairTest extends TestCase
{
    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testOrder() {
        Long one = new Long(1);
        Long two = new Long(2);
        assertFalse(one == two);
        Pair<Long, Long> pair = new Pair<Long, Long>(one, two);
        assertTrue(one == pair.first());
        assertTrue(two == pair.last());
    }

    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testEquality1() {
        assertTrue(new Pair<String, String>(null, null).equals(new Pair<String, String>(null, null)));
    }

    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testEquality2() {
        assertFalse(new Pair<String, String>(null, null).equals(new Pair<String, String>(null, "foo")));
    }

    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testEquality3() {
        assertFalse(new Pair<String, String>(null, null).equals(new Pair<String, String>("foo", "foo")));
    }

    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testEquality4() {
        assertFalse(new Pair<String, String>(null, null).equals(new Pair<String, String>("foo", null)));
    }

    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testEquality5() {
        assertFalse(new Pair<String, String>(null, "foo").equals(new Pair<String, String>(null, null)));
    }

    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testEquality6() {
        assertFalse(new Pair<String, String>("foo", "foo").equals(new Pair<String, String>(null, null)));
    }

    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testEquality7() {
        assertFalse(new Pair<String, String>("foo", null).equals(new Pair<String, String>(null, null)));
    }

    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testEquality8() {
        assertFalse(new Pair<String, String>("foo", null).equals(new Pair<String, String>("faa", null)));
    }

    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testEquality9() {
        assertTrue(new Pair<String, String>("foo", null).equals(new Pair<String, String>("foo", null)));
    }

    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testEquality10() {
        assertTrue(new Pair<String, String>("foo", "hello").equals(new Pair<String, String>("foo", "hello")));
    }

    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testCompareTo() {
        assertTrue(new Pair<Integer, Integer>(1, 1).compareTo(new Pair<Integer, Integer>(1, 1)) ==0);
        assertTrue(new Pair<Integer, Integer>(1, 2).compareTo(new Pair<Integer, Integer>(1, 1)) >0);
        assertTrue(new Pair<Integer, Integer>(1, 1).compareTo(new Pair<Integer, Integer>(1, 2)) <0);
        assertTrue(new Pair<Integer, Integer>(2, 1).compareTo(new Pair<Integer, Integer>(1, 2)) >0);
        assertTrue(new Pair<Integer, Integer>(1, 1).compareTo(new Pair<Integer, Integer>(2, -1)) <0);

        Pair<Integer, Integer> p11= new Pair<Integer, Integer>(1,1);
        Pair<Integer, Integer> p11b= new Pair<Integer, Integer>(1,1);
        Pair<Integer, Integer> n11= new Pair<Integer, Integer>(-1,-1);
        Pair<Integer, Integer> n11b= new Pair<Integer, Integer>(1,-1);
        Pair<Integer, Integer> p12= new Pair<Integer, Integer>(1,2);
        Pair<Integer, Integer> p13= new Pair<Integer, Integer>(1,3);

        Pair<Integer, Integer> pn3= new Pair<Integer, Integer>(null,3);
        Pair<Integer, Integer> p1n= new Pair<Integer, Integer>(1,null);
        Pair<Integer, Integer> pnn= new Pair<Integer, Integer>(null,null);
        Pair<Integer, Integer> p21= new Pair<Integer, Integer>(2,1);
        Pair<Integer, Integer> p22= new Pair<Integer, Integer>(2,2);
        Pair<Integer, Integer> p22b= new Pair<Integer, Integer>(2,2);

        // equals
        assertTrue(0 == p22.compareTo(p22b));
        assertTrue(0 == p22b.compareTo(p22));
        assertTrue(0 == p11.compareTo(p11b));
        assertTrue(0 == p11b.compareTo(p11));

        // pair.first
        assertTrue(1 == p21.compareTo(p13));
        assertTrue(-1 == p13.compareTo(p21));

        // pair.last
        assertTrue(-1 == p11.compareTo(p12));
        assertTrue(1 == p12.compareTo(p11));

        // null first

        try {
            assertTrue(1 == p11.compareTo(pn3));
            fail();
        } catch (NullPointerException e) {
        }
        try {
            assertTrue(-1 == pn3.compareTo(p11));
            fail();
        } catch (NullPointerException e) {
        }

        // null last
        try {
            assertTrue(1 == p11.compareTo(p1n));
            fail();
        } catch (NullPointerException e) {
        }
        try {
            assertTrue(-1 == p1n.compareTo(p11));
            fail();
        } catch (NullPointerException e) {
        }

        // null vs negative
        try {
            assertTrue(1 == n11.compareTo(pnn));
            fail();
        } catch (NullPointerException e) {
        }
        try {
            assertTrue(1 == n11.compareTo(pn3));
            fail();
        } catch (NullPointerException e) {
        }
        try {
            assertTrue(1 == n11b.compareTo(p1n));
            fail();
        } catch (NullPointerException e) {
        }
        try {
            assertTrue(-1 == pnn.compareTo(n11));
            fail();
        } catch (NullPointerException e) {
        }
        try {
            assertTrue(-1 == pn3.compareTo(n11));
            fail();
        } catch (NullPointerException e) {
        }
        try {
            assertTrue(-1 == p1n.compareTo(n11b));
            fail();
        } catch (NullPointerException e) {
        }
    }
}
