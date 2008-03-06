package com.flaptor.util.remote;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;
import java.util.Arrays;

import com.flaptor.util.Execute;
import com.flaptor.util.TestCase;
import com.flaptor.util.TestInfo;
import com.flaptor.util.remote.RmiCodeGeneration;
import com.flaptor.util.remote.RmiServer;

public class RmiCodeGenerationTest extends TestCase{
    private RmiServer server;
    private MyInterface myInterface;
    
    @TestInfo(testType = TestInfo.TestType.UNIT)//, requiresPort = {5000}
    public void testCodeGeneration() throws FileNotFoundException {
        server = new RmiServer(5000);
        Remote remoteHandler = RmiCodeGeneration.remoteHandler(
            "remote.MyInterface",
            new Class[]{MyInterface.class},
            new MyInterface() {
            public String hello(int i) throws FileNotFoundException {
                return "hola";
            }
        });
        server.addHandler("context", remoteHandler);
        server.start();

        Remote remote = RmiUtil.getRemoteService("localhost", 5000, "context");
        
        myInterface = (MyInterface) RmiCodeGeneration.proxy(remote, new Class[]{MyInterface.class});
        assertEquals("hola", myInterface.hello(0));
        
        myInterface = (MyInterface) RmiCodeGeneration.reconnectableStub("remote.MyInterface", new Class[]{MyInterface.class}, "context", "localhost", 5000, new ExponentialFallbackPolicy());
        assertEquals("hola", myInterface.hello(0));

        Execute.stop(server);
    }

    @TestInfo(testType = TestInfo.TestType.UNIT)//, requiresPort = {5000}
    public void testException() throws FileNotFoundException {
        server = new RmiServer(5000);
        Remote remoteHandler = RmiCodeGeneration.remoteHandler(
                "remote.MyInterface",
                new Class[]{MyInterface.class},
                new MyInterface() {
                public String hello(int i) throws FileNotFoundException {
                    throw new FileNotFoundException("my exception!");
                }
            });
        Remote remoteHandler2 = RmiCodeGeneration.remoteHandler(
                "remote.MyInterface2",
                new Class[]{MyInterface.class},
                new MyInterface() {
                public String hello(int i) throws FileNotFoundException {
                    throw new ArrayIndexOutOfBoundsException();
                }
            });
        server.addHandler("context", remoteHandler);
        server.addHandler("context2", remoteHandler2);
        server.start();
        
        Remote remote = RmiUtil.getRemoteService("localhost", 5000, "context");
        myInterface = (MyInterface) RmiCodeGeneration.proxy(remote, new Class[]{MyInterface.class});
        try {
            myInterface.hello(0);
            fail("an exception should have occurred");
        } catch (FileNotFoundException e) {
            assertEquals("my exception!", e.getMessage());
        }

        myInterface = (MyInterface) RmiCodeGeneration.reconnectableStub("remote.MyInterface", new Class[]{MyInterface.class}, "context", "localhost", 5000, new ExponentialFallbackPolicy());
        try {
            myInterface.hello(0);
            fail("an exception should have occurred");
        } catch (FileNotFoundException e) {
            assertEquals("my exception!", e.getMessage());
        }

        Remote remote2 = RmiUtil.getRemoteService("localhost", 5000, "context2");
        myInterface = (MyInterface) RmiCodeGeneration.proxy(remote2, new Class[]{MyInterface.class});
        try {
            myInterface.hello(0);
            fail("an exception should have occurred");
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        myInterface = (MyInterface) RmiCodeGeneration.reconnectableStub("remote.MyInterface2", new Class[]{MyInterface.class}, "context2", "localhost", 5000, new ExponentialFallbackPolicy());
        try {
            myInterface.hello(0);
            fail("an exception should have occurred");
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        
        Execute.stop(server);
    }
    
    public static interface MyInterface {
        String hello(int i) throws FileNotFoundException;
    }
}
