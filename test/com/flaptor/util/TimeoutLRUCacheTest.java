package com.flaptor.util;

/**
 * Tests for {@link TimeoutLRUCache}
 */
public class TimeoutLRUCacheTest extends TestCase {
	private TimeoutLRUCache<String, String> cache;

	public void setUp() {
		cache = new TimeoutLRUCache<String, String>(2, 100);
	}

	public void tearDown() {	
	}
	
    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testClear() {
		cache.put("","");
		cache.clear();
		assertTrue(null == cache.get(""));
	}

    @TestInfo(testType = TestInfo.TestType.UNIT)
	public void testContains() {
		cache.put("","");
		assertTrue(null != cache.get(""));
	}

    @TestInfo(testType = TestInfo.TestType.UNIT)
	public void testOverflow() {
		cache.put("0", "0");
		cache.put("1", "1");
		cache.put("2", "2");
		assertTrue(null  == cache.get("0"));
	}

    @TestInfo(testType = TestInfo.TestType.UNIT)
	public void testHitRatioCounters() {
		cache.put("0", "0");
		cache.get("0"); //a hit
		cache.get(""); // a miss
		cache.clear();
		cache.get(""); //another miss
		float threshold = 0.00000001F;
		assertTrue((cache.getHitRatio()- 0.33333333333333F) < threshold );
		assertEquals(cache.getRecentHitRatio(), 0.0F);
	}

    int keyNumber;
    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testInTime() throws Throwable {
        keyNumber = 0;
        TestThread thread = new TestThread() {
            boolean running=true;
            public void runTest() {
                while(running) {
                    try {Thread.sleep(100);} catch (InterruptedException e) {}
                    cache.put("key"+keyNumber, "value"+keyNumber);
                    keyNumber++;
                }
            }
            public void kill() {
                running = false;
            }
        };
        thread.start();
        for (int i = 0; i < 1000; i++) {
            try {Thread.sleep(10);} catch (InterruptedException e) {}
            int times = 0;
            for (int j = 0; j < 1000; ++j) {
                if (cache.get("key"+j) != null) times++;
            }
            assert(times < 3);
        }
        thread.kill();
    }
}

