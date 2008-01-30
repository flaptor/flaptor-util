package com.flaptor.util.remote;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;

import com.flaptor.util.Execute;
import com.flaptor.util.TestCase;
import com.flaptor.util.TestInfo;
import com.flaptor.util.remote.RmiCodeGeneration;
import com.flaptor.util.remote.RmiServer;

public class RmiCodeGenerationTest extends TestCase{
    private RmiServer server;
    private MyInterface myInterfaceStub;
    
//    @TestInfo(testType = TestInfo.TestType.UNIT, requiresPort = {5000})
    public void testCodeGeneration() throws FileNotFoundException {
        server = new RmiServer(5000);
        Remote remoteHandler = RmiCodeGeneration.remoteHandler(new MyInterface() {
            public String hello(int i) throws FileNotFoundException {
                return "hola";
            }
        });
        server.addHandler("context", remoteHandler);
        server.start();

        Remote remote = RmiUtil.getRemoteService("localhost", 5000, "context");
        myInterfaceStub = (MyInterface) RmiCodeGeneration.proxy(remote, MyInterface.class);

        Execute.stop(server);
        assertEquals("hola", myInterfaceStub.hello(0));

    }

//    @TestInfo(testType = TestInfo.TestType.UNIT, requiresPort = {5000})
    public void testException() throws FileNotFoundException {
        server = new RmiServer(5000);
        Remote remoteHandler = RmiCodeGeneration.remoteHandler(new MyInterface() {
            public String hello(int i) throws FileNotFoundException {
                throw new FileNotFoundException("my exception!");
            }
        });
        server.addHandler("context", remoteHandler);
        server.start();
        
        Remote remote = RmiUtil.getRemoteService("localhost", 5000, "context");
        myInterfaceStub = (MyInterface) RmiCodeGeneration.proxy(remote, MyInterface.class);


        try {
            myInterfaceStub.hello(0);
            fail("an exception should have occurred");
        } catch (FileNotFoundException e) {
            assertEquals("my exception!", e.getMessage());
        }
        Execute.stop(server);
        
    }

    public static interface MyInterface {
        String hello(int i) throws FileNotFoundException;
    }
}
