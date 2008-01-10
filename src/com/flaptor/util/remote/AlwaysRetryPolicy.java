package com.flaptor.util.remote;

/**
 * A simple IRetryPolicy that signals to retry without delay after every failure.
 */
public class AlwaysRetryPolicy implements IRetryPolicy {

    private boolean lastSuccess = false;
    
    public boolean callServer() {
        return true;
    }

    public void markFailure() {
        lastSuccess = false;
    }

    public void markSuccess() {
        lastSuccess = true;
    }

    public boolean reconnect() {
        return !lastSuccess;
    }

}
