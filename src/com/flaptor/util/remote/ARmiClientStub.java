package com.flaptor.util.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.apache.log4j.Logger;

import com.flaptor.util.Execute;



public abstract class ARmiClientStub extends AClientStub {
   
    private static final Logger logger = Logger.getLogger(Execute.whoAmI());

    private final String serviceName = RmiServer.DEFAULT_SERVICE_NAME;
    private final boolean waitForPolicy;
    private boolean remoteInitialized;

    public ARmiClientStub(int port, String host, IRetryPolicy policy) {
        this(port,host,policy,false);
        
    }

    public ARmiClientStub(int port, String host, IRetryPolicy policy, boolean wait) {
        super(port,host,policy);
        this.waitForPolicy = wait;
    }

    private void connect() throws RemoteException{
    
        logger.debug("connecting to " + host + ":" + port);
    	Remote remote = RmiUtil.getRemoteService(host, port, serviceName);
    	if (null != remote) {
    	    remoteInitialized = true;
    		this.setRemote(remote);
    	} else {
    		logger.error("Could not get remote service: " + serviceName + "@" + host + ":" + port);
    		throw new RemoteException("Could not get remote service: " + serviceName + "@" + host + ":" + port);
    	}
    };


    public void checkConnection() throws RemoteException{
        if (policy.reconnect()) {
            try {
                connect();
            } catch (RemoteException e) {
                connectionFailure();
                throw e;
            }
        }

        // If policy does not allow to connect
        if (!policy.callServer())  {
            // if we have to wait for the policy to allow us
            if (waitForPolicy) {
                //wait
                while (!policy.callServer()) {
                    Execute.sleep(100);
                }
            // otherwise, just fail
            } else {
                throw new RemoteException();
            }
        }

        if (!remoteInitialized) {
            throw new RemoteException("Remote server never reached.");
        }
    }

    public void connectionSuccess() {
        policy.markSuccess();
    }
    public void connectionFailure() {
        policy.markFailure();
    }


    protected abstract void setRemote(Remote stub);

}
