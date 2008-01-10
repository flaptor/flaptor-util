package com.flaptor.util;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;

/**
 * This class provides best-practice execution patterns to some common commands.
 */
public final class Execute {

    //so that it cannot be instantiated
    private Execute() {}

    private static final Logger defaultLogger = Logger.getLogger(Execute.whoAmI());

    /**
     * Executes the method close with no parameters on the object received. If
     * the object is null, it does nothing (should this be logged?). Any
     * exception thrown by the method invocation is logged as a warning (what
     * else can we do?).
     * 
     * Logs are logged by the default logger.
     * 
     * @param o the object to be closed
	 * @todo use Closeable
     */
    public static void close(Object o) {
        close(o, defaultLogger);
    }

    /**
     * Executes the method close with no parameters on the object received. If
     * the object is null, it does nothing (should this be logged?). Any
     * exception thrown by the method invocation is logged as a warning (what
     * else can we do?).
     * 
     * It allows the caller to specify a different logger.
     * 
     * @param o the object to be closed
     * @param logger logger to use
	 * @todo close discards null objects, see if this requires a warning
     */
    public static void close(Object o, Logger logger) {
        // discards null objects (exception thrown in object creation?)
        if (o == null) {
            return;
        }
        Class<?> c = o.getClass();
        Method m = null;
        try {
            m = c.getMethod("close", new Class[0]);
        } catch (Exception e) {
            logger.error("Received object (of class " + c.getName()
                    + ") doesn't implement close() method", e);
            return;
        }
        try {
            m.invoke(o, new Object[0]);
        } catch (IllegalAccessException e) {
            //ignore this. It's probably caused by an input stream from a file inside
            //a jar.
        } catch (Exception e) {
            logger.warn("Error ocurred while closing object (of class "
                    + c.getName() + ")", e);
            return;
        }
    }
    
    /**
     * This method returns the fully qualified name of the class where it is invoked.
     * The string returned is the same as the one returned by getClass().getName() on an
     * instantiated object.
     * It works even when invoked from a static code.
     * Its main use is to identify the class using a log4j logger.
     *
     * This implementation's performance is not very good.
     *
     * The intended use is:
     *
     * private static final Logger logger = Logger.getLogger(Execute.whoAmI());
     */
    public static String whoAmI() {
        return new Throwable().getStackTrace()[1].getClassName();
    }

    /**
     * Returns the unqualified name of the invoking class.
     */
    public static String whatIsMyName() {
        String name = whoCalledMe();
        return name.substring(name.lastIndexOf(".")+1);
    }

    /**
     * This method returns the fully qualified name of the class that invoked the caller.
     * The string returned is the same as the one returned by getClass().getName() on an
     * instantiated object.
     */
    public static String whoCalledMe() {
        return new Throwable().getStackTrace()[2].getClassName();
    }


    // Auxiliary object to synchronize a static method.
    private static byte[] synchObj = new byte[1];

    /**
     * This method prints a stack trace.
     */
    public static void printStackTrace() {
        synchronized (synchObj) {
            int level = 0;
            System.out.println("Stack Trace:");
            for (StackTraceElement e : new Throwable().getStackTrace()) {
                if (level++>0) {
                    System.out.println("  ["+Thread.currentThread().getName()+"] "+e);
                }
            }
        }
    }


    /**
     * This method puts the invoker thread to sleep.
     * This implementation wraps Thread.sleep() in a try catch exception, logging the 
     * InterruptedException in the default logger.
     * @param millis the time in milliseconds to sleep
     */
    public static void sleep(long millis) {
        Execute.sleep(millis, defaultLogger);
    }

    /**
     * This method puts the invoker thread to sleep.
     * This implementation wraps Thread.sleep() in a try catch exception, logging the 
     * InterruptedException in the default logger.
     * @param millis the time in milliseconds to sleep
     * @param logger it logs the occurrence of an InterruptedException
     */
    public static void sleep(long millis, Logger logger) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            logger.error("Interrupted ", e);
        }
    }

}

