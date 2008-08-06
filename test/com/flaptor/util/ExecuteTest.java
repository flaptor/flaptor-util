package com.flaptor.util;


/**
 * Tests for {@link Execute}
 */
public class ExecuteTest extends TestCase {

    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testClassContext() {
        Class<?>[] context = Execute.getClassContext();
        assertEquals(Execute.class, context[1]);
        assertEquals(ExecuteTest.class, context[2]);
    }
    
    @TestInfo(testType = TestInfo.TestType.UNIT)
    public static void testMyClassStatic() {
        assertEquals(ExecuteTest.class, Execute.myClass());
        ExecuteTestCalled.staticCheckMyClass();
    }
    public static void testWhoAmIStatic() {
        assertEquals("com.flaptor.util.ExecuteTest", Execute.whoAmI());
        ExecuteTestCalled.staticCheckWhoAmI();
    }
    
    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testMyClass() {
        assertEquals(ExecuteTest.class, Execute.myClass());
        new ExecuteTestCalled().checkMyClass();
    }
    public void testWhoAmI() {
        assertEquals("com.flaptor.util.ExecuteTest", Execute.whoAmI());
        new ExecuteTestCalled().checkWhoAmI();
    }
    public void testWhoCalledMe() {
        new ExecuteTestCalled().checkWhoCalledMe();
    }
    public void testMyCallersClass() {
        new ExecuteTestCalled().checkMyCallersClass();
    }
    
}

class ExecuteTestCalled {
    static void staticCheckMyClass() {
        TestCase.assertEquals(ExecuteTestCalled.class, Execute.myClass());
        ExecuteTestSubcalled.staticCheckMyClass();
    }
    static void staticCheckMyCallersClass() {
        TestCase.assertEquals(ExecuteTestCalled.class, Execute.myCallersClass(0));
        TestCase.assertEquals(ExecuteTest.class, Execute.myCallersClass());
        TestCase.assertEquals(ExecuteTest.class, Execute.myCallersClass(1));
        ExecuteTestSubcalled.staticCheckMyCallersClass();
    }
    static void staticCheckWhoAmI() {
        TestCase.assertEquals("com.flaptor.util.ExecuteTestCalled", Execute.whoAmI());
        ExecuteTestSubcalled.staticCheckWhoAmI();
    }
    static void staticCheckWhoCalledMe() {
        TestCase.assertEquals("com.flaptor.util.ExecuteTest", Execute.whoCalledMe());
        ExecuteTestSubcalled.staticCheckWhoCalledMe();
    }
    void checkMyClass() {
        TestCase.assertEquals(ExecuteTestCalled.class, Execute.myClass());
        new ExecuteTestSubcalled().checkMyClass();
    }
    void checkMyCallersClass() {
        TestCase.assertEquals(ExecuteTestCalled.class, Execute.myCallersClass(0));
        TestCase.assertEquals(ExecuteTest.class, Execute.myCallersClass());
        TestCase.assertEquals(ExecuteTest.class, Execute.myCallersClass(1));
        new ExecuteTestSubcalled().checkMyCallersClass();
    }
    void checkWhoAmI() {
        TestCase.assertEquals("com.flaptor.util.ExecuteTestCalled", Execute.whoAmI());
        new ExecuteTestSubcalled().checkWhoAmI();
    }
    void checkWhoCalledMe() {
        TestCase.assertEquals("com.flaptor.util.ExecuteTest", Execute.whoCalledMe());
        new ExecuteTestSubcalled().checkWhoCalledMe();
    }
}

class ExecuteTestSubcalled {
    public static void staticCheckMyClass() {
        TestCase.assertEquals(ExecuteTestSubcalled.class, Execute.myClass());
    }
    public static void staticCheckMyCallersClass() {
        TestCase.assertEquals(ExecuteTestSubcalled.class, Execute.myCallersClass(0));
        TestCase.assertEquals(ExecuteTestCalled.class, Execute.myCallersClass());
        TestCase.assertEquals(ExecuteTestCalled.class, Execute.myCallersClass(1));
        TestCase.assertEquals(ExecuteTest.class, Execute.myCallersClass(2));
    }
    public static void staticCheckWhoAmI() {
        TestCase.assertEquals("com.flaptor.util.ExecuteTestSubcalled", Execute.whoAmI());
    }
    public static void staticCheckWhoCalledMe() {
        TestCase.assertEquals("com.flaptor.util.ExecuteTestCalled", Execute.whoCalledMe());
    }
    public void checkMyClass() {
        TestCase.assertEquals(ExecuteTestSubcalled.class, Execute.myClass());
    }
    public void checkMyCallersClass() {
        TestCase.assertEquals(ExecuteTestSubcalled.class, Execute.myCallersClass(0));
        TestCase.assertEquals(ExecuteTestCalled.class, Execute.myCallersClass());
        TestCase.assertEquals(ExecuteTestCalled.class, Execute.myCallersClass(1));
        TestCase.assertEquals(ExecuteTest.class, Execute.myCallersClass(2));
    }
    public void checkWhoAmI() {
        TestCase.assertEquals("com.flaptor.util.ExecuteTestSubcalled", Execute.whoAmI());
    }
    public void checkWhoCalledMe() {
        TestCase.assertEquals("com.flaptor.util.ExecuteTestCalled", Execute.whoCalledMe());
    }
}