package com.flaptor.util;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Tests for {@link MultiExecutor}
 * 
 * @author Martin Massera
 */
public class MultiExecutorTest extends TestCase{
    
    Integer num = new Integer(0);
    
    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testMultiExecutor() {
        MultiExecutor<Integer> m = new MultiExecutor<Integer>(5, "hola");
        Execution<Integer> e = new Execution<Integer>();

        Set<Integer> set = new HashSet<Integer>();
        int numTasks = 100;
        for (int i = 0; i < numTasks; ++i){
            Callable<Integer> t = new Callable<Integer>() {
                public Integer call() throws Exception {
                    Thread.sleep(10);
                    synchronized(num) {
                        num = num + 1;
                        return num;
                    }
                }
            };
            e.getTaskQueue().add(t);
            set.add(i+1);
        }        
        m.addExecution(e);
        while(true) {
            synchronized (e) {
                if (e.getResultsList().size() == numTasks) break;
            }
            try {Thread.sleep(10);} catch (InterruptedException e1) {}
        }
        for (Execution.Results<Integer> res : e.getResultsList()) {
            assertTrue(res.isFinishedOk());
            assertTrue(set.contains(res.getResults()));
            set.remove(res.getResults());
        }
        assertTrue(set.isEmpty());
    }
}
