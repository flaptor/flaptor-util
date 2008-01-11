package com.flaptor.util.remote;

import java.util.HashMap;
import java.util.Map;

import com.flaptor.util.TestCase;
import com.flaptor.util.TestInfo;

public class AServerTest extends TestCase {
    
    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testValidatePortRange() {
        try {
            new TestServer(0);
            fail();
        } catch (IllegalArgumentException e) {}

        try {
            new TestServer(65537);
            fail();
        } catch (IllegalArgumentException e) {}
        
        try {
            new TestServer(2000);
        } catch (Exception e) {
            fail("could not initiate server with a valid port");
        }
    }
    
    private static class TestServer extends AServer {
        public TestServer(final int port) {
            super(port);
        }
        protected void startServer() {
        }
		protected Map<String, ? extends Object> getHandlers() {
			return new HashMap<String, Object>();
		}
		protected void requestStopServer() {
		}
    }

    //TODO test that stop and keeprunning work, after Aserver has System.exit removed from stopper
}

