package com.flaptor.util.remote;

/**
 * The client-side version of a remote class called through some rpc
 * mechanism.
 * The concrete subclasses should implement the actual methods exported
 * by rpc, but those methods can throw ConnectionException when the connection fails.
 * @see ConnectionException
 *
 */
public abstract class AClientStub {
    protected final IRetryPolicy policy;
    protected final int port;
    protected final String host;
    
    protected AClientStub(final int port, final String host, IRetryPolicy policy) {
        this.port = port;
        this.host = host;
        this.policy = policy;
    }
}
