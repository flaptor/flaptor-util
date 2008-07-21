package com.flaptor.util;

/**
 * Test for {@link PortUtil}
 *
 * @author dbuthay
 */
public class PortUtilTest extends TestCase {

    private Config config;

    protected void setUp(){
        try { 
            TestUtils.setConfig("common.properties","");    
            config = TestUtils.getConfig();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testOffset(){

        int PORT = 123;
        String portname = "portutiltest";

        config.set("port.offset."+portname,String.valueOf(PORT));

        assertEquals("offset port differ",PORT,PortUtil.getOffset(portname));
    }


    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testBasePort(){
        int PORT = 10000;
        config.set("port.base",String.valueOf(PORT));

        assertEquals("base port differs",PORT,PortUtil.getBasePort());
    }

    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testBasePortWithOffset(){
        int BASE_PORT = 10000;
        int PORT = 123;
        String portname = "portutiltest";

        config.set("port.base",String.valueOf(BASE_PORT));
        config.set("port.offset."+portname,String.valueOf(PORT));

        assertEquals("port differs",BASE_PORT + PORT,PortUtil.getPort(portname));
    }


    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testParseHost(){

        String host = "somehost";
        int BASE_PORT = 10000;
        config.set("port.base",String.valueOf(BASE_PORT));

        Pair<String,Integer> parsed = PortUtil.parseHost(host);

        assertEquals("host differs",host,parsed.first());
        assertEquals("port is not baseport",BASE_PORT,(int)parsed.last());
    }


    /** Tests that BASE_PORT does not care if port is defined on host String */
    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testParseHostWithPort(){

        String host = "somehost";
        int BASE_PORT = 10000;
        int PORT = 20000;
        config.set("port.base",String.valueOf(BASE_PORT));
        String hostWithPort = host + ":" + PORT;


        Pair<String,Integer> parsed = PortUtil.parseHost(hostWithPort);
        assertEquals("host differs",host,parsed.first());
        // this is the important line
        assertEquals("port is not defined port",PORT,(int)parsed.last());
    }


    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testParseHostAndPortOffset(){
        String host = "somehost";
        int BASE_PORT = 10000;
        int PORT = 20000;
        int PORT_OFFSET = 123;
        String portname= "portutiltest";
        config.set("port.offset."+portname,String.valueOf(PORT_OFFSET));
        config.set("port.base",String.valueOf(BASE_PORT));
        String hostWithPort = host + ":" + PORT;


        Pair<String,Integer> parsed = PortUtil.parseHost(hostWithPort,portname);
        assertEquals("host differs",host,parsed.first());
        // this is the important line
        assertEquals("port differs",PORT + PORT_OFFSET,(int)parsed.last());
    }

}
