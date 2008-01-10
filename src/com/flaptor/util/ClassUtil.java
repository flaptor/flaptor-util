package com.flaptor.util;

/**
 * class related utils 
 */
public class ClassUtil {
	
	/**
	 *   
	 * @param className
	 * @return an instance of the specified class, using the empty constructor
	 * @throws RuntimeException if there is a problem
	 */
	public static Object instance(String className) {
		try {
			return Class.forName(className).newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
