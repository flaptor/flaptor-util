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
