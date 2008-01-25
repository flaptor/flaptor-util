package com.flaptor.util;

/**
 * Tests for {@link NetUtil}
 */
public class NetUtilTest extends TestCase {
	public void setUp() {
	}

	public void tearDown() {
	}
	
    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testInvalidConstructor() {
        try {
            for (String ip : NetUtil.getLocalIPs()) {
                if (ip.equals("127.0.0.1")) return;
            }
            fail("The loopback device is not among the reported interfaces.");
        } catch (Exception e) {
            fail("UnexpectedException caught.");
        }
    }
    

}

