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

package com.flaptor.util.remote;

/**
 * Implements a retry policy that, after a fail, waits before reconnecting
 * in exponentially increasing intervals, up to a maximum of 4 seconds.
 *
 */
public class ExponentialFallbackPolicy implements IRetryPolicy {
    private static final long MIN_WAIT = 1;
    private static final long MAX_WAIT = 4096; //Approx. 4 seconds.

    private long failTime = 0;
    private long wait = 0;
    private boolean skipping = true;
    
    public synchronized boolean callServer() {
        if (skipping) {
            return false;
        } else {
            if (0L != wait) {
                skipping = true;
            }
            return true;
        }
    }

    public synchronized void markFailure() {
        if (0L == wait) {
            wait = MIN_WAIT;
            skipping = true;
        } else {
            wait *= 2;
            if (wait > MAX_WAIT) {
                wait = MAX_WAIT;
            }
        }
        failTime = System.currentTimeMillis();
    }

    public synchronized void markSuccess() {
        skipping = false;
        wait = 0L;
        failTime = 0L;
    }

    public synchronized boolean reconnect() {
        if (!skipping) {
            return false;
        } else {
            if (System.currentTimeMillis() > (failTime + wait)) {
                skipping = false;
                return true;
            }
            return false;
        }
    }

}
