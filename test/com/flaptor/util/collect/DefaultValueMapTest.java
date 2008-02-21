package com.flaptor.util.collect;

import java.util.Map;

import com.flaptor.util.TestCase;
import com.flaptor.util.TestInfo;
import com.google.common.collect.Maps;

/**
 * Test for {@link DefaultValueMap}
 * 
 * @author Santiago Perez (santip)
 */
@SuppressWarnings("serial")
public class DefaultValueMapTest extends TestCase {
    
    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testDifferentKey() {
        DefaultValueMap<String, Float> map = createZeroDefaultingMap();
        assertNull(map.get(new Object()));
    }
    
    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testDefaultValue() {
        DefaultValueMap<String, Float> map = createZeroDefaultingMap();
        assertEquals(0.0f, map.get(""));
        assertEquals(0.0f, map.get("foo"));
    }
    
    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testModifiedValue() {
        DefaultValueMap<String, Float> map = createZeroDefaultingMap();
        Float original = map.get("bar");
        map.put("bar", original + 1);
        assertEquals(1.0f, map.get("bar"));
    }

    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testInsertedValue() {
        DefaultValueMap<String, Float> map = createZeroDefaultingMap();
        map.put("bar", 2.0f);
        assertEquals(2.0f, map.get("bar"));
    }
    
    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testIdentityMap() {
        DefaultValueMap<String, String> map = createIdentityMap();
        assertEquals("", map.get(""));

        map.put("foobar", "barfoo");
        assertEquals("barfoo", map.get("foobar"));
        
        assertEquals("bar", map.get("bar"));
        assertEquals("foo", map.get("foo"));
        
        map.put("foo", "bar");
        assertEquals("bar", map.get("foo"));
    }
    
    
    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testBackingMap() {
        Map<String, String> backingMap = Maps.newHashMap();
        backingMap.put("foobar", "barfoo");
        
        DefaultValueMap<String, String> map = createIdentityMap(backingMap);
        assertEquals("barfoo", map.get("foobar"));
        assertEquals("foo", map.get("foo"));
        backingMap.put("foo", "bar");
        assertEquals("bar", map.get("foo"));
        map.put("foo", "foobar");
        assertEquals("foobar", map.get("foo"));
        
        assertEquals("bar", map.get("bar"));
    }
    
    private DefaultValueMap<String, String> createIdentityMap(Map<String, String> backingMap) {
        return new DefaultValueMap<String, String>(String.class, backingMap) {
            @Override
            protected String getDefaultValue(String key) throws Exception {
                return key;
            }
        };
    }
    
    private DefaultValueMap<String, String> createIdentityMap() {
        return new DefaultValueMap<String, String>(String.class) {
            @Override
            protected String getDefaultValue(String key) throws Exception {
                return key;
            }
        };
    }
    
    private DefaultValueMap<String, Float> createZeroDefaultingMap() {
        return new DefaultValueMap<String, Float>(String.class) {
            @Override
            protected Float getDefaultValue(String key) throws Exception {
                return 0.0f;
            }
        };
    }

}
