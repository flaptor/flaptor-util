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

import java.util.Queue;
import java.util.concurrent.Callable;

import com.flaptor.util.Execution.Results;

/**
 * A worker thread of the MultiExecutor. It peeks the executions from the queue. 
 * If the execution is empty it discards it, otherwise it executes the next task 
 * of that execution
 *
 * @param <T> the return type of the execution tasks 
 */
public class MultiExecutorWorker<T> extends com.flaptor.util.AStoppableThread {
    Queue<Execution<T>> executionQueue;
    
    public MultiExecutorWorker(Queue<Execution<T>> executionQueue, String threadName) {
        thrd.setName(threadName);
        thrd.setDaemon(true);
        this.executionQueue = executionQueue;
    }

    public void run() {
        while (true) {
            if (signaledToStop) {
                stopped = true;
                return;
            }
            Execution<T> execution = null;
            Callable<T> task = null;
            
            synchronized(executionQueue) {
                execution = executionQueue.peek();
                if (execution == null) {
                    sleep(10);
                    continue;
                }
                synchronized(execution) {
                    if (execution.getTaskQueue().isEmpty() || execution.isForgotten()) { 
                        executionQueue.poll();
                        execution.notifyAll();
                        continue;
                    } else {
                        task = execution.getTaskQueue().poll();
                    }
                }
            }
            
            Results<T> results = new Results<T>();
            try {
                results.setResults(task.call());
                results.setFinishedOk(true);
            } catch (Throwable t) {
                results.setException(t);
            }
            
            synchronized(execution) {
                execution.getResultsList().add(results);
                execution.notifyAll();
            }
        }
    }
}
