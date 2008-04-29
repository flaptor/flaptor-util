package com.flaptor.util;

/**
 * Test for {@link MonitoringKillerThread}
 */
public class MonitoringKillerThreadTest extends TestCase {


    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testKills() {
        StoppableMock toMonitor = new StoppableMock();
        StoppableMock toKill = new StoppableMock();

        new MonitoringKillerThread(toMonitor,toKill).start();
        
        toMonitor.requestStop();
        Execute.sleep(2*1000); // monitors every second .. 2 seconds should be enough

        assertTrue(toKill.isStopped());
    }




    private class StoppableMock implements Stoppable {
    
        private boolean stopped = false;

        public void requestStop() {
            this.stopped = true;
        }

        public boolean isStopped() {
            return stopped;
        }
    }
}
