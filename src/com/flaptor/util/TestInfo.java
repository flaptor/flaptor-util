/**
 * 
 */
package com.flaptor.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TestInfo {
    public static enum TestType { UNIT, INTEGRATION, SYSTEM }
    TestType testType();
    /**if the test makes use of a port it should be specified here. An empty array
     * means no port is needed.*/
    int[] requiresPort() default {};
}
