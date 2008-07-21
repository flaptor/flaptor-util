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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.flaptor.util.Execute;
import com.flaptor.util.FileUtil;
import com.flaptor.util.Pair;
import com.flaptor.util.TestCase;
import com.flaptor.util.TestInfo;
import com.flaptor.util.remote.RmiServer;

/**
 * Test for {@link MultiCache}
 * 
 * @author Martin Massera
 */
public class MultiCacheTest extends TestCase{
    
    private int startPort = 30000;
    private final int NUM_SERVERS = 5;
    private List<FileCache<String>> caches;
    String[] multiCacheTestDirsPath= new String[NUM_SERVERS];

    public void tearDown() throws Exception{
        super.tearDown();
        for (String path: multiCacheTestDirsPath){
            FileUtil.deleteDir(path);
        }
    }
    
    private int getPort(int i) {
    	return 30000 + i;
    }
    
    //This is an integration test because it uses rmi. It could use a fake rpc...
    @TestInfo(testType = TestInfo.TestType.INTEGRATION,
            requiresPort = {30000, 30001, 30002, 30003, 30004})
    public void testFind() throws UnsupportedEncodingException {
        caches = new ArrayList<FileCache<String>>();
        List<RmiServer> servers = new ArrayList<RmiServer>();
        List<Pair<String, Integer>> hosts = new ArrayList<Pair<String,Integer>>();
        for (int i = 0; i < NUM_SERVERS; ++i) {
            multiCacheTestDirsPath[i] = FileUtil.createTempDir("testmulticache", ".tmp").getAbsolutePath();
            FileCache<String> cache = new FileCache<String>(multiCacheTestDirsPath[i]);
            
            caches.add(cache);
            hosts.add(new Pair<String, Integer>("localhost", getPort(i)));
            
            FileCacheTest.addRandomKeys(cache, 50);
            RmiServer server = new RmiServer(30000 + i);
            server.addHandler(RmiServer.DEFAULT_SERVICE_NAME, cache);
            server.start();
            servers.add(server);
        }
        
        MultiCache<String> multiCache = new MultiCache<String>(hosts, 60000, 20);
        assertEquals(null, multiCache.getItem("hola"));
        assertFalse(multiCache.hasItem("hola"));
        
        caches.get(new Random().nextInt(NUM_SERVERS)).addItem("hola", "como estas");
        assertEquals("como estas", multiCache.getItem("hola"));
        assertTrue(multiCache.hasItem("hola"));
        
        caches.get(new Random().nextInt(NUM_SERVERS)).addItem("hola", "como estas");
        caches.get(new Random().nextInt(NUM_SERVERS)).addItem("hola", "como estas");
        caches.get(new Random().nextInt(NUM_SERVERS)).addItem("hola", "como estas");
        assertTrue(multiCache.hasItem("hola"));
        assertEquals("como estas", multiCache.getItem("hola"));

        for (RmiServer server: servers) {
            Execute.stop(server);
        }
    }
}
