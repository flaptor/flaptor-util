package com.flaptor.util.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRmiEchoService extends Remote {

        public int remoteEcho(int data) throws RemoteException;
        public String remoteEcho(String data) throws RemoteException;

}

