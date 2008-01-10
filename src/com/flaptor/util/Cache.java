package com.flaptor.util;

/**
 * This class defines a generic caching mechanism.
 * It also provides information about hits and misses.
 * The subclasses of this class must implement get, put and override clear.
 */
public abstract class Cache<K,V> {

	private long totalMisses = 0;
	private long recentMisses = 0;
	private long totalHits = 0;
	private long recentHits = 0;
	
	/**
	 * Gets a cached object from the provided key.
	 * The subclass implementing get must call either markHit() or markMiss()
	 * for every call, in order to update the hits/misses counters.
	 * @return the cached object, if it is in the cache. Null otherwise.
	 */
	public abstract V get(K key);
	
	/**
	 * Inserts a new object to be cached.
	 */
	public abstract V put(K key, V value);
	
	/**
	 * Resets the cache.
	 * After this call, the cache is empty.
	 * Any subclass of cache must call this method to reset the hits/misses
	 * counters.
	 */
	public void clear() {
		recentMisses = 0;
        recentHits = 0;
	}
	
	/**
	 * Returns the ratio of hits to misses from the last clear.
	 * @return the hit ratio or NaN, if this cache has never been used since
	 * 	the last clear.
	 */
	public synchronized final float getRecentHitRatio() {
		return (float)(((double)recentHits) / (((double)recentHits) + ((double)recentMisses)));
	}

	/**
	 * Returns the ratio of hits to misses since the creation of this cache.
	 * @return the hit ratio or NaN, if this cache has never been used.
	 */
	public synchronized final float getHitRatio() {
		return (float)(((double)totalHits) / (((double)totalHits) + ((double)totalMisses)));
	}

	/**
	 * Updates the hits counters.
	 * All subclasses must call this method once for every hit.
	 */
	protected synchronized final void markHit() {
		totalHits++;
		recentHits++;
	}
	 
	/**
	 * Updates the misses counters.
	 * All subclasses must call this method once for every miss.
	 */
	protected synchronized final void markMiss() {
		totalMisses++;
		recentMisses++;
	}

}

