package com.flaptor.util;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Tests for {@link Config}
 */
public class ConfigTest extends TestCase {

    private final String configName = "configTest.tmp";
    private final String configNameDefaults = "configTest.tmp.defaults";
    private final String configName2 = "configTest2.tmp";
    private final String configName3 = "configTest3.tmp";
    private final String configName4 = "configTest4.tmp";

    private final String configText1 = "test1=true\ntest2=1\n";
    private final String configText2 = "#import configTest.tmp.defaults\ntest1=false\ntest3=foo\ntest";
    private final String configText3 = "#import configTest.tmp.defaults\n#test1=false\n#test2=3\ntest4=lalala\n" + 
                                       "#comment\ntest3=oof\ntest5=hola,\\\nchau,\\\npepe=1\n" + 
                                       "test6=hola,\\\nchau,\\\npepe=1\n";
    private final String configText4 = "#import " + configName3;

    private Config config;
    private Config config2;
    private Config config3;

    public void setUp() {
        writeConfigFile(configNameDefaults, configText1);
        writeConfigFile(configName, configText2);
        config = Config.getConfig(configName);
        writeConfigFile(configName2, configText1);
        config2 = Config.getConfig(configName2);
        writeConfigFile(configName3, configText3);
        config3 = Config.getConfig(configName3);
        writeConfigFile(configName4, configText4);
        // Execute.sleep(1000);
    }

    public void tearDown() {
        config = null;
        FileUtil.deleteFile(configName);
        FileUtil.deleteFile(configName2);
        FileUtil.deleteFile(configName3);
        FileUtil.deleteFile(configName4);
        FileUtil.deleteFile(configNameDefaults);
    }

    private void writeConfigFile(String filename, String content) {
        BufferedOutputStream fos = null;
        try {
            fos = new BufferedOutputStream(new FileOutputStream(filename));
            fos.write(content.getBytes());
        } catch (Exception e) {
            System.out.println("Error while trying to write the properties file." + e);
        } finally {
            Execute.close(fos);
        }
    }

    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testConfigWithDefaults() {
        assertTrue("Checking for an overridden property didn't return true", config.isDefined("test1"));
        assertTrue("Default value was not overridden", config.getBoolean("test1") == false);
        assertTrue("Checking for a default property didn't return true", config.isDefined("test2"));
        assertTrue("Default value was not present", config.getInt("test2") == 1);
        assertTrue("Checking for a primary property didn't return true", config.isDefined("test3"));
        assertEquals("Primary file was not loaded", config.getString("test3"), "foo");
        assertFalse("Checking for a non-existent property didn't return false", config.isDefined("test4"));
        try {
            config.getString("test4");
            fail("Non-existent property didn't throw an exception");
        } catch (IllegalStateException e) {
            // Expected!
        } catch (Exception e) {
            fail("Non-existent property didn't throw IllegalStateException, it threw " + e.getClass().getName() + " instead");
        }
        try {
            config.getInt("test1");
            fail("String value requested as int didn't throw an exception");
        } catch (IllegalArgumentException e) {
            // Expected!!
        } catch (Exception e) {
            fail("String value requested as int dind't throw IllegalArgumentException, it threw " + e.getClass().getName() + " instead");
        }
    }

    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testConfigWithoutDefaults() {
        assertTrue("Error reading boolean variable", config.getBoolean("test1") == false);
        assertTrue("Error reading int value", config.getInt("test2") == 1);
    }

    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testNoFiles() {
        filterOutput("Couldn't read resource non_existent_file.properties");
        try {
            Config config = Config.getConfig("non_existent_file.properties");
            fail("Non existent file didn't throw an exception");
        } catch (IllegalStateException e) {
            // Expected
        } catch (Exception e) {
            fail("Non existent file didn't throw IllegalStateException, it threw " + e.getClass().getName() + " instead");
        }
        unfilterOutput();
    }

    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testSaveToDisk() throws IOException {
        config3.set("test1", "true");
        config3.set("test2", "666");
        config3.set("test3", "bar");
        config3.set("test5", "chau,\\\nhola,\\\npepe=1");
        config3.saveToDisk();

        Config config4 = Config.getConfig(configName4);
        assertEquals(true, config4.getBoolean("test1"));
        assertEquals(666, config4.getInt("test2"));
        assertEquals("bar", config4.getString("test3"));
        assertEquals("lalala", config4.getString("test4"));
        assertEquals("hola", config4.getStringArray("test5")[1]);
        assertEquals("pepe=1", config4.getStringArray("test6")[2]);
        
        try {
            config4.getInt("pepe");
            fail("pepe should not be defined");
        } catch (IllegalStateException e) {
        }
    }

    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testPairList() {

        StringBuffer sb = new StringBuffer();
        int MAX = 10;
        for (int i = 0; i < MAX; i++) {
            sb.append("key");
            sb.append(i);
            sb.append(":");
            sb.append("value");
            sb.append(i);
            sb.append(",");
        }

        config.set("pairTest", sb.substring(0, sb.length() - 1));

        List<Pair<String, String>> list = config.getPairList("pairTest");

        for (int i = 0; i < MAX; i++) {
            Pair<String, String> pair = list.get(i);
            assertEquals(pair.first(), "key" + i);
            assertEquals(pair.last(), "value" + i);
        }

        assertEquals(MAX, list.size());
    }

    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testIllegalPairList() {
        String illegalConfig1 = "key:value,,key1:value";
        config.set("pairTest", illegalConfig1);
        try {
            // extracted from error string on Config.java,
            // for this key-value pair validation
            super.filterOutput("', ,'");
            config.getPairList("pairTest");
            fail("empty definition does not fail.");
        } catch (IllegalArgumentException e) {
            super.unfilterOutput();
            // success
        }

        String illegalConfig2 = "key:value:othervalue,key1:value";
        config.set("pairTest", illegalConfig2);
        try {
            // extracted from error string on Config.java
            // for this key-value pair validation
            super.filterOutput("that is not a pair");
            config.getPairList("pairTest");
            fail("Definition with various values does not fail.");
        } catch (IllegalArgumentException e) {
            super.unfilterOutput();
            // success
        }
    }

}
