package com.flaptor.util.rmi;

import java.rmi.RemoteException;

public class RmiEchoService implements IRmiEchoService {

    public int remoteEcho(int data) throws RemoteException {
        return data;
    }

    public String remoteEcho(String data) throws RemoteException {
        return data;
    }

}

