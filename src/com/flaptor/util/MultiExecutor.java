/*
Copyright 2008 Flaptor (flaptor.com) 

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at 

    http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
See the License for the specific language governing permissions and 
limitations under the License.
*/

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
 * 
 * @author Martin Massera
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
