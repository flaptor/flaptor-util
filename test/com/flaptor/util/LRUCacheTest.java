package com.flaptor.util;

/**
 * Tests for {@link LRUCache}
 */
public class LRUCacheTest extends TestCase {
	private LRUCache<String, String> cache;

	public void setUp() {
		cache = new LRUCache<String, String>(2);
	}

	public void tearDown() {
		cache = null;
	}
	
    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testInvalidConstructor() {
        try {
            new LRUCache(0);
            fail();
        } catch (IllegalArgumentException e) {
        }
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

}

