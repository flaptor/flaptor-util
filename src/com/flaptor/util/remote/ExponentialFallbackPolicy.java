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
    private static final long MIN_WAIT = 128;
    private static final long MAX_WAIT = 16384; //Approx. 16 seconds.
    

    private long failTime = 0;
    private long wait = 0;
    private CallingState callingState = CallingState.SKIP;
    private ConnectionState connectionState = ConnectionState.DISCONNECTED; 
    
    
    public synchronized boolean callServer() {
    	return callingState == CallingState.CALL;
    }

    public synchronized void markFailure() {
        if (callingState == CallingState.CALL) { //first failure
            wait = MIN_WAIT;
            failTime = System.currentTimeMillis();
            callingState = CallingState.SKIP;
            connectionState = ConnectionState.DISCONNECTED;
        } else { //not the first failure
        	if (connectionState == ConnectionState.RECONECTING) {
        		wait *= 2;
        		if (wait > MAX_WAIT) {
        			wait = MAX_WAIT;
        		}
        	}
        }
    }

    public synchronized void markSuccess() {
    	callingState = CallingState.CALL;
    	connectionState = ConnectionState.CONNECTED;
    }

    public synchronized boolean reconnect() {
    	if (connectionState == ConnectionState.CONNECTED || connectionState == ConnectionState.RECONECTING) {
    		return false;
    	}
        if (System.currentTimeMillis() > (failTime + wait)) {
            connectionState = ConnectionState.RECONECTING;
            return true;
        } else {
        	return false;
        }
    }
    
    private enum CallingState {CALL, SKIP};
    private enum ConnectionState {CONNECTED, DISCONNECTED, RECONECTING};

}
