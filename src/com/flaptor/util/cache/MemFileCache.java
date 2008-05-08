package com.flaptor.util.cache;

import com.flaptor.util.LRUCache;

/**
 * Cache that first looks in memory and if it doesnt find it goes to disk
 * @author Martin Massera
 *
 * @param <T>
 */
public class MemFileCache<T> {
   
    final private FileCache<T> fileCache;
    final private LRUCache<String, T> memCache;
    
    public MemFileCache(int memCacheMaxSize, String fileCacheDir) {
        memCache = new LRUCache<String, T>(memCacheMaxSize);
        fileCache = new FileCache<T>(fileCacheDir);
    }
    
    T get(String key) {
        T val = memCache.get(key);
        if (val != null) return val;
        else return fileCache.getItem(key);
    }

    void put(String key, T value) {
        memCache.put(key, value);
        fileCache.addItem(key, value);
    }
}
