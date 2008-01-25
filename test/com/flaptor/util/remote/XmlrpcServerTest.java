package com.flaptor.util.remote;

import java.net.URL;

import org.apache.log4j.Logger;

import com.flaptor.util.Execute;
import com.flaptor.util.TestCase;
import com.flaptor.util.TestInfo;

/**
 * Tests for {@link XmlrpcServer}
 * 
 * @author Martin Massera
 */
public class XmlrpcServerTest extends TestCase
{
    private static final Logger logger = Logger.getLogger(Execute.whoAmI());
    private static final int port = 3049;
    private EchoSender snd = null;
    private EchoServer srv = null;

    protected void setUp(){
        srv = new EchoServer();
        snd = new EchoSender();
    }

    protected void tearDown(){
    	srv.srv.requestStop();
        while (!srv.srv.isStopped()) {
            Execute.sleep(20);
        }
    	srv = null;
    	snd = null;
    }

    @TestInfo(testType = TestInfo.TestType.UNIT,
            requiresPort = {port})
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
        String echoed = snd.send(original);
        String intermediate = srv.lastReceived;
        logger.debug("Intermediate String: " + intermediate);
        logger.debug("Echoed String: " + echoed);
        assertTrue("Original and echoed string differ.", original.equals(echoed));
        assertTrue("Original and intermediate string differ.", original.equals(intermediate));
    }

    //Private classes.
    public class EchoServer{
		public XmlrpcServer srv = null;
		public String lastReceived = null;
		
		EchoServer(){
		    srv = new XmlrpcServer(3049);
		    srv.addHandler(null, this);
		    srv.start();
		}
		
		public String echo(String s){
		    lastReceived = s;
		    return s;
		}
    }

    class EchoSender{
		private XmlrpcClient xmlrpc = null;
		
		EchoSender(){
		    try{
		    	xmlrpc = new XmlrpcClient(new URL("http://127.0.0.1:3049"));
		    } catch (Exception e) {
		    	logger.error("Error in EchoSender constructor: " + e);
		    }
		}
		
		public String send(String s){
		    String retString = null;
		    try{
			retString = xmlrpc.execute(null, "echo", new Object[]{s}).toString();
		    } catch (Exception e) {
			logger.error("Error while sending message: " + e);
		    }
		    return(retString);
		}
    }
}
