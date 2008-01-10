package com.flaptor.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A LRUcache that removes elements after a timeout 
 */
public class TimeoutLRUCache<K,V> extends LRUCache<K,V> {

    private Map<K,Long> timeoutMap = new HashMap<K,Long>();
    
    public TimeoutLRUCache(int maxSize, final int elementTimeout) {
        super(maxSize);
        final int period = elementTimeout/5 < 500 ? 500 : elementTimeout / 5;
        new Timer(true).scheduleAtFixedRate(new TimerTask(){
            public void run() {
                synchronized (timeoutMap) {
                    Iterator<Map.Entry<K, Long>> it = timeoutMap.entrySet().iterator();
                    while(it.hasNext()) {
                        Map.Entry<K, Long> entry = it.next();
                        if (System.currentTimeMillis() - entry.getValue() > elementTimeout) {
                            it.remove();
                            map.remove(entry.getKey());
                        }
                    }
                }
            }
        }, 0, period);
    }

    @Override
    public void clear() {
        synchronized (timeoutMap) {
            timeoutMap.clear();
            super.clear();
        }
    }

    @Override
    public V put(K key, V value) {
        synchronized (timeoutMap) {
            timeoutMap.put(key, System.currentTimeMillis());
            return super.put(key, value);
        }
    }

}
