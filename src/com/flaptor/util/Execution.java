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
import java.util.concurrent.Callable;

/**
 * Represents the execution of a group of tasks. Maintains a list of 
 * tasks to be executed (executionQueue) and a list of
 * results from tasks already executed (resultsList).
 * 
 * If the task of the execution are no longer meant to be executed, the 
 * execution can be "forgotten" 
 * 
 * @param <T> the type of the results
 * 
 * @author Martin Massera
 */
public class Execution<T> {
    
    protected Queue<Callable<T>> executionQueue = new LinkedList<Callable<T>>();
    protected List<Results<T>> resultsList = new ArrayList<Results<T>>();
    protected boolean forgotten = false;
    
    public Queue<Callable<T>> getTaskQueue() {
        return executionQueue;
    }

    public List<Results<T>> getResultsList() {
        return resultsList;
    }

    public boolean isForgotten() {
        return forgotten;
    }

    /**
     * No more tasks will be executed and the execution will be removed
     * of the execution list
     */
    public void forget() {
        forgotten = true;
    }
   
    /**
     * represents the outcome of the execution of a task, which can 
     * finish ok or not (with exceptions). It holds the results or 
     * the exception thrown
     * 
     * @param <T> the type of the result
     */
    public static class Results<T> {
        private Callable<T> task;
        private boolean finishedOk = false;
        private T results = null;
        private Throwable exception = null;
        
        public Results(Callable<T> task) {
            super();
            this.task = task;
        }

        public Callable<T> getTask() {
            return task;
        }
                
        public boolean isFinishedOk() {
            return finishedOk;
        }
        public void setFinishedOk(boolean finishedOk) {
            this.finishedOk = finishedOk;
        }
        public Throwable getException() {
            return exception;
        }
        public void setException(Throwable exception) {
            this.exception = exception;
        }
        public T getResults() {
            return results;
        }
        public void setResults(T results) {
            this.results = results;
        }
    }
}
