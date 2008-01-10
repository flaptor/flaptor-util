package com.flaptor.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * utility class for thread related stuff
 */
public class ThreadUtil {

	private static Logger logger = Logger.getLogger(Execute.whoAmI());

	/**
	 * @return a list of live threads
	 */
	public static List<Thread> getLiveThreads() {
		List<Thread> ret = new ArrayList<Thread>();
		ret.addAll(Thread.getAllStackTraces().keySet());
		return ret;
	}
	/**
	 * @return a list of live thread names
	 */
	public static List<String> getThreadNames() {
		List<String> ret = new ArrayList<String>();
		for (Thread t : ThreadUtil.getLiveThreads()) {
			ret.add(t.getName());
		}
		return ret;
	}
	
	/**
	 * sleeps without interruption exception
	 */
	public static void sleep(int millis) {
		long start = System.currentTimeMillis();
		while (true) {
			long toWait = millis + start - System.currentTimeMillis();
			if (toWait < 0) return;
			try {
				Thread.sleep(toWait);
				return;
			} catch (InterruptedException e) {e.printStackTrace();}
		}
	}
}
