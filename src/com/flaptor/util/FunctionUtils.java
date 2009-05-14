package com.flaptor.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.google.common.base.Function;

public class FunctionUtils {

    public static Function<Object, String> getToString() {
        return new Function<Object, String>() {
            public String apply(Object from) {
                return String.valueOf(from);
            }
        };
    }

    public static <T> Function<String, T> getValueOf(final Class<T> type) {
        return getStaticMethod(type, String.class, type, "valueOf");
    }
    
    public static <F, T> Function<F, T> getStaticMethod(Class<?> type, Class<F> fromType, final Class<T> toType, String methodName) {
        try {
            final Method method = type.getMethod(methodName, fromType);
            return new Function<F, T>() {
                public T apply(F from) {
                    try {
                        return toType.cast(method.invoke(null, from));
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static Object invokeMethod(Object target, String method) {
        return getMethodInvoker(method).apply(target);
    }
    
    public static Function<Object, Object> getMethodInvoker(final String methodName) {
        return getMethodInvoker(Object.class, methodName);
    }

    public static <T> Function<Object, T> getMethodInvoker(final Class<T> returnType, final String methodName) {
        return new Function<Object, T>() {
            public T apply(Object from) {
                if (from == null) return null;
                try {
                    Method method = from.getClass().getMethod(methodName, new Class[0]);
                    return returnType.cast(method.invoke(from, new Object[0]));
                } catch (SecurityException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
    
    
}
