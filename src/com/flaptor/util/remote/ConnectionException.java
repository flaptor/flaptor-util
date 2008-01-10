package com.flaptor.util.remote;

/**
 * An exception thrown by a stub when an rpc method fails.
 */
public class ConnectionException extends Exception {
    public ConnectionException() {
    }
    
    public ConnectionException(Throwable cause) {
        super(cause);
    }

    public ConnectionException(String msg) {
        super(msg);
    }
}
