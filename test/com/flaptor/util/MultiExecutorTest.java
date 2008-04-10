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
    private MultiExecutor<Integer> m = new MultiExecutor<Integer>(100, "testMultiExecutor");
    
    Integer num = new Integer(0);
    
    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testMultiExecutor() throws InterruptedException {
        Execution<Integer> e = new Execution<Integer>();

        Set<Integer> set = new HashSet<Integer>();
        int numTasks = 100;
        for (int i = 0; i < numTasks; ++i){
            e.addTask(new Callable<Integer>() {
                public Integer call() throws Exception {
                    Thread.sleep(10);
                    synchronized(num) {
                        num = num + 1;
                        return num;
                    }
                }
            });
            set.add(i+1);
        }
        m.addExecution(e);
        e.waitFor(-1);
        for (Execution.Results<Integer> res : e.getResultsList()) {
            assertTrue(res.isFinishedOk());
            assertTrue(set.contains(res.getResults()));
            set.remove(res.getResults());
        }
        assertEquals(0, e.getProblems().size());
        assertTrue(set.isEmpty());
    }
    
    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testEmptyExecution() throws InterruptedException {
        Execution<Integer> e = new Execution<Integer>();
        m.addExecution(e);
        e.waitFor();
        assertTrue(e.hasFinished());
    }
    
}
