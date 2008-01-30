package com.flaptor.util.remote;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.ServerException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

import com.flaptor.util.Execute;
import com.flaptor.util.IOUtil;
import com.flaptor.util.Pair;
import com.flaptor.util.Triad;

/**
 * Class for automatically generating Rmi boilerplate code.
 * 
 * @author Martin Massera
 *
 */
public class RmiCodeGeneration {
    private static Logger logger = Logger.getLogger(Execute.whoAmI());
    private static AtomicInteger classCounter = new AtomicInteger(0);
    
    private static final String THROWABLE_IN_REMOTE_HOST_CODE = "caught throwable in remote host code";
    
    /**
     * it gives you an object with the same methods as the original one,
     * but implementing Remote and throwing RemoteException in all its methods. 
     * It can handle exceptions so they are sent all the way to the client (calling) code.
     * 
     * @param originalHandler
     * @return  
     */
    public static Remote remoteHandler(final Object originalHandler) {
        try {
            logger.debug("creating remote server for  " + originalHandler);
            
            String name = "dynamicallyGeneratedRmiHandler.class" + classCounter.incrementAndGet() + "." + originalHandler.getClass().getCanonicalName();     
            logger.debug("interface name " + name);
            
            ClassPool cp = ClassPool.getDefault();
            CtClass interf = cp.makeInterface(name);
            interf.setSuperclass(cp.get(Remote.class.getCanonicalName()));

            List<Method> objectMethods = Arrays.asList(Object.class.getMethods());
            for (Method method : originalHandler.getClass().getMethods()) {
                if (objectMethods.contains(method)) {
                    logger.debug("skipping method from object: " + method);
                    continue;
                }
                logger.debug("processing method " + method.getName());
                
                Class retType = method.getReturnType();
                CtClass newRetType = cp.get(retType.getCanonicalName());
                Class[] paramTypes = method.getParameterTypes();
                CtClass[] newParamTypes = new CtClass[paramTypes.length];
                for (int i = 0; i < paramTypes.length; ++i) newParamTypes[i] = cp.get(paramTypes[i].getCanonicalName());

                CtMethod newMethod = new CtMethod(newRetType, method.getName(),newParamTypes, interf);
                newMethod.setExceptionTypes(new CtClass[] {cp.get(RemoteException.class.getCanonicalName())});
                interf.addMethod(newMethod);
            }
            Class handlerInterface = interf.toClass();
            
            logger.debug("constructing method map");
            final Map<Method, Method> methodMap = getMethodMap(handlerInterface, originalHandler.getClass());
            
            InvocationHandler handler = new InvocationHandler() {
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    Method originalMethod = methodMap.get(method);
                    logger.debug("invoking original method " + originalMethod + " for " + method);
                    try {
                        return originalMethod.invoke(originalHandler, args);
                    } catch (InvocationTargetException e) {
                        logger.debug("wrapping throwable in RemoteException");
                        throw new RemoteException(THROWABLE_IN_REMOTE_HOST_CODE, e.getCause());
                    }
                }
            };
            return (Remote)Proxy.newProxyInstance(handlerInterface.getClassLoader(), new Class[]{handlerInterface}, handler);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Given a remote, creates a proxy of the provided interface that calls methods in that remotes
     * If exceptions are thrown in the remote server code, they are thrown through the original interface
     * as the original exception. Other exceptions should be caught as RemoteException
     * 
     * @param remote
     * @param originalInterface
     * @return 
     */
    public static Object proxy(final Remote remote, Class originalInterface) {
        logger.debug("constructing reverse method map");
        final Map<Method, Method> methodMap = getMethodMap(originalInterface, remote.getClass());
        InvocationHandler handler = new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Method originalMethod = methodMap.get(method);
                logger.debug("invoking original method " + originalMethod + " for " + method);
                try {
                    return originalMethod.invoke(remote, args);
                } catch (InvocationTargetException e) {
                    Throwable t = e.getCause();
                    if (t instanceof ServerException) {
                        if (t.getCause() instanceof RemoteException && t.getCause().getMessage().contains(THROWABLE_IN_REMOTE_HOST_CODE)) {
                            throw t.getCause().getCause();
                        }
                    }
                    logger.warn("unexpected exception", t);
                    throw t;
                }
            }
        };
        return Proxy.newProxyInstance(originalInterface.getClassLoader(), new Class[]{originalInterface}, handler);
    }
        
    private static Map<Method, Method> getMethodMap(Class c1, Class c2) {
        final Map<Method, Method> methodMap = new HashMap<Method, Method>();
        for (Method calledMethod : c1.getMethods()) {
            for (Method originalMethod : c2.getMethods()) {
                if (calledMethod.getName().equals(originalMethod.getName())) {
                    Class[] params1 = calledMethod.getParameterTypes();
                    Class[] params2 = originalMethod.getParameterTypes();
                    if (params1.length != params2.length) continue;
                                            
                    boolean equals = true;
                    for (int i = 0; i < params1.length; ++i) {
                        if (!params1[i].getCanonicalName().equals(params2[i].getCanonicalName())) {
                            equals = false;
                            break;
                        }
                    }
                    if (equals) {
                        logger.debug(calledMethod + " ->" + originalMethod);
                        methodMap.put(calledMethod, originalMethod);
                    }
                }
            }
        }
        return methodMap;
    }
}
