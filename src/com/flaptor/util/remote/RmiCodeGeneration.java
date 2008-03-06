package com.flaptor.util.remote;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import org.apache.log4j.Logger;

import com.flaptor.util.Execute;

/**
 * Class for automatically generating Rmi boilerplate code.
 * 
 * @author Martin Massera
 *
 */
public class RmiCodeGeneration {
    private static Logger logger = Logger.getLogger(Execute.whoAmI());
    
    private static final String THROWABLE_IN_REMOTE_HOST_CODE = "caught throwable in remote host code";
    private static final Map<String, Class> classMap = new HashMap<String, Class>(); 
    
    /**
     * it gives you an object with the same methods as the original one,
     * but implementing Remote and throwing RemoteException in all its methods. 
     * It can handle exceptions so they are sent all the way to the client (calling) code.
     * 
     * @param remoteClassName the fully qualified name of the generated remote interface. The returned handler will 
     * be of this class name. This is important because on the client side, RMI loads this interface (you can generate it
     * using getRemoteInterface or using reconnectableStub)
     * @param interfaces the interfaces that the object will expose
     * @param originalHandler
     * @return  
     */
    public static Remote remoteHandler( String remoteClassName, Class[] interfaces, final Object originalHandler) {
        try {
            logger.debug("creating remote server for  " + originalHandler);
            Class handlerInterface = getRemoteInterface(remoteClassName, interfaces);

            logger.debug("constructing method map");
            final Map<Method, Method> methodMap = getMethodMap(new Class[]{handlerInterface}, originalHandler.getClass());
            
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

    public static Class getRemoteInterface(String remoteClassName, Class[] interfaces) {
        try{ 
            ClassPool cp = ClassPool.getDefault();
            logger.debug("interface name " + remoteClassName);
            Class handlerInterface = classMap.get(remoteClassName);
            if (handlerInterface == null) {
                CtClass generatedInterf = cp.makeInterface(remoteClassName);
                generatedInterf.setSuperclass(cp.get(Remote.class.getCanonicalName()));
                List<Method> objectMethods = Arrays.asList(Object.class.getMethods());
                for (Class interf : interfaces) {
                    for (Method method : interf.getMethods()) {
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
        
                        CtMethod newMethod = new CtMethod(newRetType, method.getName(),newParamTypes, generatedInterf);
                        newMethod.setExceptionTypes(new CtClass[] {cp.get(RemoteException.class.getCanonicalName())});
                        generatedInterf.addMethod(newMethod);
                    }
                }
                handlerInterface = generatedInterf.toClass();
                classMap.put(remoteClassName, handlerInterface);
            }
            return handlerInterface;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    /**
     * Given a remote, creates a proxy of the provided interface that calls methods in that remote
     * If exceptions are thrown in the remote server code, they are thrown through the original interface
     * as the original exception. Other exceptions should be caught as RemoteException
     * 
     * @param remote
     * @param originalInterface
     * @return 
     */
    public static Object proxy(final Remote remote, Class[] originalInterfaces) {
        logger.debug("constructing reverse method map");
        final Map<Method, Method> methodMap = getMethodMap(originalInterfaces, remote.getClass());
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
        return Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), originalInterfaces, handler);
    }
    
    private static Map<Method, Method> getMethodMap(Class[] calledClasses, Class originalClass) {
        final Map<Method, Method> methodMap = new HashMap<Method, Method>();
        for (Class calledClass : calledClasses) {
            for (Method calledMethod : calledClass.getMethods()) {
                for (Method originalMethod : originalClass.getMethods()) {
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
        }
        return methodMap;
    }
    
    /**
     * generates a reconnectable stub
     * 
     * @param objectInterface
     * @param context context of the rmi handler, if null uses default
     * @param host rmi server host
     * @param port rmi server port
     * @param policy
     * @return an object that implements objectInterface
     */
    public static Object reconnectableStub(String remoteClassName, final Class[] interfaces, String context, String host, int port, IRetryPolicy policy) {
        //need to generate the remote interface because RMI will instantiate it
        getRemoteInterface(remoteClassName, interfaces);
        final StubbingInvocationHandler stubbingInvocationHandler = new StubbingInvocationHandler();
        final ARmiClientStub myStub = new ARmiClientStub(port, host, context, policy) {
            protected void setRemote(Remote stub) {
                stubbingInvocationHandler.setObjectProxy(proxy(stub, interfaces));
            }
        };
        stubbingInvocationHandler.setRmiStub(myStub);
        return Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), interfaces, stubbingInvocationHandler);
    }

    private static class StubbingInvocationHandler implements InvocationHandler {
        private ARmiClientStub rmiStub;
        private Object objectProxy;
        public void setRmiStub(ARmiClientStub rmiStub) {
            this.rmiStub = rmiStub;
        }
        public void setObjectProxy(Object objectProxy) {
            this.objectProxy = objectProxy;
        }
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try { 
                rmiStub.checkConnection();
                Object ret = method.invoke(objectProxy, args);
                rmiStub.connectionSuccess();
                return ret;
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof RemoteException) { 
                    logger.error(e,e);
                    rmiStub.connectionFailure();
                    throw new ConnectionException(e);
                } else {
                    //other exceptions were thrown in the server code, we must throw them
                    rmiStub.connectionSuccess();
                    throw e.getCause();
                }
            }
        }
    };
}
