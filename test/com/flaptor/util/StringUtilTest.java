package com.flaptor.util;

import java.util.Vector;


/**
 * Test for {@link StringUtil}
 *
 * @author dbuthay
 */
public class StringUtilTest extends TestCase {

    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testUrlEncode(){
        String pre = "this is my test";
        String post = "this+is+my+test";

        assertEquals("strings differ",post,StringUtil.urlEncode(pre));
    }


    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testUrlDecode(){
        String post = "this is my test";
        String pre = "this+is+my+test";

        assertEquals("strings differ",post,StringUtil.urlDecode(pre));
    }


    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testNullToEmpty(){
        String param = "some string";
        String nullParam = null;

        assertEquals("modified not null string!", param,StringUtil.nullToEmpty(param));
        assertEquals("null string converted to something different to \"\"","",StringUtil.nullToEmpty(nullParam));
    }

    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testEmptyToNull(){
        String param = "some string";
        String emptyParam = "";
        String spaces = "   ";


        // without trimming
        assertEquals("modified not empty string",param,StringUtil.emptyToNull(param,false));
        assertEquals("modified not empty string",spaces,StringUtil.emptyToNull(spaces,false));
        assertEquals("did not null empty string",null,StringUtil.emptyToNull(emptyParam,false));

        // trimming
        assertEquals("modified not empty string",param,StringUtil.emptyToNull(param,true));
        assertEquals("did not null spaces string",null,StringUtil.emptyToNull(spaces,true));
        assertEquals("did not null empty string",null,StringUtil.emptyToNull(emptyParam,true));

    }


    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testJoin(){
        Vector<String> vector = new Vector<String>();
        vector.add("this");
        vector.add("is");
        vector.add("my");
        vector.add("test");
        
        assertEquals("wrong join","this is my test",StringUtil.join(vector," "));
        assertEquals("wrong join","thisismytest",StringUtil.join(vector,""));
    }
}
