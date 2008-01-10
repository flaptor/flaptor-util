package com.flaptor.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


/**
 * A threadpool of workers to execute tasks in parallel. The tasks are grouped in
 * objects of the Execution class  
 *
 * @param <T> the type of the result of the tasks
 */
public class MultiExecutor<T> {

    protected List<MultiExecutorWorker<T>> workers = new ArrayList<MultiExecutorWorker<T>>();
    protected Queue<Execution<T>> executionQueue = new LinkedList<Execution<T>>();
    
    /**
     * Constructs a MultiExecutor 
     * 
     * @param numWorkerThreads the number of worker threads
     * @param workerNamePrefix the name prefix for worker threads (for debugging purposes)
     */
    public MultiExecutor(int numWorkerThreads, String workerNamePrefix) {
        
        for (int i = 0; i < numWorkerThreads; ++i) {
            MultiExecutorWorker<T> worker = new MultiExecutorWorker<T>(executionQueue, workerNamePrefix + (i+1));            
            workers.add(worker);
            worker.start();
        }
    }
    /**
     * Adds an execution to the executionQueue
     */
    public void addExecution(Execution<T> e) {
        executionQueue.add(e);
    }
    
    /**
     * @return the number of executions in queue
     */
    public int getExecutionsLeft() {
        return executionQueue.size();
    }
}
