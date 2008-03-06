package com.flaptor.util.remote;

/**
 * Exception For signaling that an exception occurred in remote host code
 * @author Martin Massera
 *
 */
public class RemoteHostCodeException extends RpcException {
    public static final String THROWABLE_IN_REMOTE_HOST_CODE = "Throwable in remote host code";
    
    public RemoteHostCodeException(String message) {
        super(message);        
    }
    public RemoteHostCodeException(Throwable cause) {
        super(THROWABLE_IN_REMOTE_HOST_CODE, cause);
    }
}
