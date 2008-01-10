package com.flaptor.util;

public class AStoppableThreadTest extends TestCase {
    class TestThread extends AStoppableThread {
        public volatile int state = 0;

        public void run() {
            state = 1;
            while (!signaledToStop) {
                sleep(60);
            }
            state = 2;
        }
    }

    /**
     * Tests if the thread exits quickly (within five seconds) after a request to stop.
     */
    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testInterrupted() {
        TestThread t = new TestThread();
        long start = System.currentTimeMillis();
        t.start();
        Execute.sleep(100);
        t.requestStop();
        while (t.state != 2) {
            Execute.sleep(20);
        }
        long elapsed = System.currentTimeMillis() - start;
        assertTrue(elapsed < 5000);
    }
}
