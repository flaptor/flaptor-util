/*
Copyright 2008 Flaptor (flaptor.com) 

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at 

    http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
See the License for the specific language governing permissions and 
limitations under the License.
*/
package com.flaptor.util.cache;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.PropertyConfigurator;

import com.flaptor.util.FileUtil;
import com.flaptor.util.TestCase;
import com.flaptor.util.TestInfo;
import com.flaptor.util.cache.FileCache;
import com.flaptor.util.cache.RmiCacheStub;
import com.flaptor.util.remote.ConnectionException;
import com.flaptor.util.remote.RmiServer;

/**
 * Test for {@link FileCache}
 * 
 * @author Martin Massera
 */
public class FileCacheTest extends TestCase {


    private String cacheDir = FileUtil.createTempDir("testcache", ".tmp").getAbsolutePath();
    private FileCache<String> cache;
    private int MAX_KEY_COUNT = 100;

    public void setUp() {
        String log4jConfigPath = com.flaptor.util.FileUtil.getFilePathFromClasspath("log4j.properties");
        if (null != log4jConfigPath) {
            PropertyConfigurator.configureAndWatch(log4jConfigPath);
        } else {
            System.err.println("log4j.properties not found on classpath!");
        }
        cache = new FileCache<String> (cacheDir, 2);
    }

    public void tearDown() {
        File cacheDirFile = new File(cacheDir);
        FileUtil.deleteDir(cacheDirFile);
    }

    @TestInfo(testType = TestInfo.TestType.INTEGRATION)
    public void testRemote() throws RemoteException, UnsupportedEncodingException, ConnectionException {
        int port = 50500;
        RmiServer server = new RmiServer(port);
        server.addHandler(RmiServer.DEFAULT_SERVICE_NAME, cache);
        server.start();
        
        addRandomKeys(cache, 500);
        cache.addItem("hola", "como estas");

        RmiCacheStub<String> stub = new RmiCacheStub<String>(port, "localhost");
        assertTrue(stub.hasItem("hola"));
        assertFalse(stub.hasItem("chau"));

        assertEquals("como estas", stub.getItem("hola"));
        
        stub.removeItem("hola");
        stub.addItem("chau", "como estas");
        
        assertFalse(cache.hasItem("hola"));
        assertTrue(cache.hasItem("chau"));
        assertEquals("como estas", cache.getItem("chau"));
    }
    
    public static Set<String> randomKeys(int maxKeyCount) throws UnsupportedEncodingException {
        Set<String> set = new HashSet<String>();
        Random rnd = new Random(new Date().getTime());
        int count = rnd.nextInt(maxKeyCount);
        byte[] bytes = new byte[20];

        for (int i = 0; i < count ; i++ ) {
            rnd.nextBytes(bytes);
            String key =new String(bytes);
            key = new String(key.getBytes("UTF-8"));
            set.add(key);
        }

        return set;
    }
    public static Set<String> addRandomKeys(FileCache<String>  cache, int maxKeyCount) throws UnsupportedEncodingException
    {
    	Set<String> keys = randomKeys(maxKeyCount);
		for (String key: keys) {
			cache.addItem(key,key);
		}
		return keys;
    }


    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testDeleting(){
    	assertFalse(cache.iterator().hasNext());
    	try {
			Set<String> keys = addRandomKeys(cache, MAX_KEY_COUNT);
			for (String key: keys) {
		    	assertTrue(cache.iterator().hasNext());
				cache.removeItem(key);
			}
			assertFalse(cache.iterator().hasNext());
			

			keys = addRandomKeys(cache, MAX_KEY_COUNT);
			for (String key: cache) {
				cache.removeItem(key);
			}
			assertFalse(cache.iterator().hasNext());

			keys = addRandomKeys(cache, MAX_KEY_COUNT);
			Iterator<String> it = cache.iterator();
			while (it.hasNext()) {
				it.next();
				it.remove();
			}
			assertFalse(cache.iterator().hasNext());

    	} catch (Exception e) {
	    	e.printStackTrace();
	    	fail();
	    }
    }
    
    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testEverythingIsFound(){
	    for (int i = 0 ; i < 10; ++i) {
	    	try {
	    		Set<String> keys = addRandomKeys(cache, MAX_KEY_COUNT);
	
		        for (String key: keys) {
                    String value = cache.getItem(key);
		            assertEquals("retrieved data is not equal to stored data " + key, key, value);
		        }
		        
		        Iterator<String> cacheKeys = cache.iterator();
		        while (cacheKeys.hasNext()) {
		            String key = cacheKeys.next();
		            String value = cache.getItem(key);
		            assertEquals("retrieved data is not equal to stored data " + key, key, value);
		            
		            keys.remove(key);
		        }
	        
		        assertEquals(0,keys.size());
	    	} catch (Exception e) {
		        	fail();	
		    }
    	}
    }


    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testCollitions() {
        cache = new FileCache<String> (cacheDir,true); //generate collitions
        // smaller than default, as using 100 would make this test very 
        // slow, and the point is to prove that collisions are solved.
        MAX_KEY_COUNT = 10; 
        testEverythingIsFound();
    }
}
