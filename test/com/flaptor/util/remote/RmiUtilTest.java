package com.flaptor.util.remote;

import java.rmi.RemoteException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.flaptor.util.TestInfo;
import com.flaptor.util.rmi.IRmiEchoService;
import com.flaptor.util.rmi.RmiTestSetup;

/**
 * Tests for {@link RmiUtil}
 */
public class RmiUtilTest extends TestCase {
    
    private static final int TEST_VAL_INT_5 = 5;

    /**
     * Needed to start a registry and register a service only once and use it for the testGetRemoteServiceXxxx.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(RmiUtilTest.class);
        return new RmiTestSetup(suite);
    }

    @TestInfo(testType = TestInfo.TestType.UNIT,
            requiresPort = {RmiTestSetup.REGISTRY_PORT})
    public void testGetRemoteServiceOk() throws RemoteException {
        try {
            IRmiEchoService remoteService = (IRmiEchoService)RmiUtil.getRemoteService(RmiTestSetup.REGISTRY_HOSTNAME,RmiTestSetup.REGISTRY_PORT,RmiTestSetup.REMOTE_SERVICE_NAME);
            assertNotNull("Remote service must not be null", remoteService);
            int ret = remoteService.remoteEcho(TEST_VAL_INT_5);
            assertTrue("Remote service returned a bad value (" +ret+ ") instead of " +TEST_VAL_INT_5, ret == TEST_VAL_INT_5);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception " +e);
        }
    }

    @TestInfo(testType = TestInfo.TestType.UNIT,
            requiresPort = {RmiTestSetup.REGISTRY_PORT})
    public void testGetRemoteServiceWithBadPortError() {
        IRmiEchoService remoteService = (IRmiEchoService)RmiUtil.getRemoteService(RmiTestSetup.REGISTRY_HOSTNAME,(RmiTestSetup.REGISTRY_PORT+1),RmiTestSetup.REMOTE_SERVICE_NAME);
        assertNull("Got something requesting at invalid registry port", remoteService);
    }

    @TestInfo(testType = TestInfo.TestType.UNIT,
            requiresPort = {RmiTestSetup.REGISTRY_PORT})
    public void testGetRemoteServiceWithBadNameError() {
        IRmiEchoService remoteService = (IRmiEchoService)RmiUtil.getRemoteService(RmiTestSetup.REGISTRY_HOSTNAME,RmiTestSetup.REGISTRY_PORT,RmiTestSetup.REMOTE_SERVICE_NAME+"_BAD");
        assertNull("Got something requesting an invalid service name", remoteService);
    }
}
