package com.flaptor.util.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.apache.log4j.Logger;

import com.flaptor.util.TestCase;
import com.flaptor.util.TestInfo;

/** @todo test that if 2 servers try to run on the same port, an understandable
 * exception is thrown*/
//TODO
public class RmiServerTest extends TestCase
{
    private static final Logger logger = Logger.getLogger(com.flaptor.util.Execute.whoAmI());

    private static final String SERVER_NAME = "localhost";
    private static final int PORT = 1140;

    private RmiServer server;
    private EchoService localEchoService;
    private EchoSender sender;

    public void setUp(){
        localEchoService = new EchoService(); // Must have a strong referentce to it, couldn't do something like new RmiServer(..., new EchoService(), ...).
        server = new RmiServer(PORT);
        server.addHandler(RmiServer.DEFAULT_SERVICE_NAME, localEchoService);
        server.start();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            // Shouldn't happen
        }
        sender = new EchoSender();
    }

    public void tearDown(){
        localEchoService = null;
        server = null;
    }

    @TestInfo(testType = TestInfo.TestType.UNIT,
            requiresPort = {PORT})
    public void testTransmission(){
        logger.debug("Testing ascii characters");
        StringBuffer buf = new StringBuffer();
        for (char i = 0; i < 128; i++){
            buf.append(i);
        }
        strings(buf.toString());
        //Now I test some latin and greek characters
        buf = new StringBuffer();
        for (char i = 128; i < 1024; i++){
            buf.append(i);
        }
        strings(buf.toString());
        //More
        buf = new StringBuffer();
        for (char i = 1024; i < 65000; i++){
            buf.append(i);
        }
        strings(buf.toString());

    }

    private void strings(String original){
        logger.debug("Original String: " + original);
        String echoed = sender.send(original);
        String intermediate = localEchoService.getLastReceived();
        logger.debug("Intermediate String: " + intermediate);
        logger.debug("Echoed String: " + echoed);
        assertTrue("Original and echoed string differ.", original.equals(echoed));
        assertTrue("Original and intermediate string differ.", original.equals(intermediate));
    }

    //------------------------------------------------------------------------------
    //Internal classes
    
    private static interface IEchoService extends Remote {
        public String remoteEcho(String s) throws RemoteException;
        //public String getLastReceived();
    }

    //Private classes.
    private static class EchoService implements IEchoService {

        private String lastReceived = null;

        public String remoteEcho(String s) throws RemoteException {
            lastReceived = s;
            return s;
        }

        public String getLastReceived() {
            return lastReceived;
        }

    }

    private static class EchoSender {

        IEchoService echoService;

        EchoSender(){
            echoService = (IEchoService)RmiUtil.getRemoteService(SERVER_NAME, PORT, RmiServer.DEFAULT_SERVICE_NAME);
        }

        public String send(String s) {
            try {
                return echoService.remoteEcho(s);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

}

