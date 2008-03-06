/*
Copyright 2008 Flaptor (flaptor.com) 

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at 

    http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
See the License for the specific language governing permissions and 
limitations under the License.
*/

package com.flaptor.util.remote;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ConnectException;
import java.net.URL;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

import com.flaptor.util.IOUtil;

/**
 * This class represents a client of XmlRpc. It provides an ease of use
 * and abstraction over the apache xml rpc client and it handles the 
 * exceptions better  
 * 
 * @author Martin Massera
 */
public class XmlrpcClient {
	private static final Logger logger = Logger.getLogger(com.flaptor.util.Execute.whoAmI());

	private XmlRpcClient client;
	private URL url;
	
	public XmlrpcClient(URL url) {
		client = new XmlRpcClient(url);
		this.url = url;
	}
	
	/**
	 * executes the xmlrpc method and returns an object, that can be a primitive type or an Object[].
	 * If an exception is thrown, it checks to see if it is a NoSuchMethodException and in that case it throws a NoSuchMethodException 
	 *   
	 * @param context the context where the service is exported. if null or empty, uses the default context defined in XmlrpcServer.DEFAULT_CONTEXT 
	 * @param methodName the name of the method (can be service.methodName if there are various services)
	 * @param params
	 * @return
	 * @throws XmlRpcFault 
	 * @throws UnsupportedOperationException if the connection succeded but there is no such method
	 * @throws XmlRpcException 
	 */
	public Object execute(String context, String methodName, Object[] params) throws XmlRpcException {
		if (params == null) params = new Object[]{};
		
		if (context == null || context.length() == 0) context = XmlrpcServer.DEFAULT_CONTEXT;
		
		String method = context+ "." + methodName;

		Vector<Object> v = new Vector<Object>();
		for (Object o: params) {
			if (XmlrpcSerialization.isSupportedClass(o)) v.add(o);
			else v.add(IOUtil.serialize(o));
		}
		
		Object ret;
		try {
			ret = client.execute(method, v);
		} catch (IOException e1) {
		    if (e1 instanceof ConnectException) {
		        throw new RpcConnectionException(e1);
		    } else throw new RuntimeException(e1);
		}
		
		if (ret instanceof XmlRpcException) {
			XmlRpcException e = (XmlRpcException)ret;
			if (e.getMessage().contains("NoSuchMethodException") || (e.getMessage().contains("RPC handler object") &&  e.getMessage().contains("not found"))) {
				throw new NoSuchRpcMethodException(method);
			} else if (e.getMessage().contains("java.lang.Exception: ")){
			    throw new RemoteHostCodeException(e.getMessage().replace("java.lang.Exception: ", "")); 
			} else {
			    throw e;
			}
		} else {
			return ret;
		}
	}

	/**
	 * creates a proxy for calling xmlrpc methods directly in java
	 *  
	 * @param serviceInterface the interface of the service
	 * @param url the url of the server
	 * @param context the context where this service is exposed, if null uses XmlrpcServer.DEFAULT_CONTEXT
	 * @return a proxy object that implements the interface given, where calls to this object
	 * will make an rpc call to the server
	 */
	static public Object proxy(final String context, Class serviceInterface, URL url) {
		XmlrpcClient client = new XmlrpcClient(url);
		return proxy(context, serviceInterface, client);
	}

	/**
	 * creates a proxy for calling xmlrpc methods directly in java
	 * 
	 * @param serviceInterface the interface of the service
	 * @param client the client to make calls to
	 * @param context the context where this service is exposed, if null uses XmlrpcServer.DEFAULT_CONTEXT
	 * @return a proxy object that implements the interface given, where calls to this object
	 * will make an rpc call to the server
	 */
	public static Object proxy(final String context, Class serviceInterface, final XmlrpcClient client) {
		final String contextFinal = context != null ? context : XmlrpcServer.DEFAULT_CONTEXT;
		InvocationHandler handler = new InvocationHandler() {
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				
				Object ret = client.execute(context, method.getName(), args);
				if (XmlrpcSerialization.isSupportedClass(method.getReturnType())) {
					return ret;
				} else {
					return IOUtil.deserialize((byte[])ret);
				}
			}
		};
		return Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class[]{serviceInterface}, handler);
	}
}
