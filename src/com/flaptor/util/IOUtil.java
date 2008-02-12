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

package com.flaptor.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;

/**
 * utility class for common IO operations
 * 
 * @author Martin Massera
 */
public class IOUtil {

    /**
     * fully reads a reader
     * @return a string with all the contents of the reader 
     * @throws IOException 
     */
    public static String readAll(Reader reader) throws IOException {
        BufferedReader br = new BufferedReader(reader);
        StringBuffer buf = new StringBuffer();
        char[] buffer = new char[256];
        while(true) {
            int charsRead = br.read(buffer);
            if (charsRead == -1) break;
            buf.append(buffer, 0, charsRead);
        }
        return buf.toString();
    }


    /**
     * fully reads a stream
     * @return a string with all the contents of the stream 
     * @throws IOException 
     */
    public static String readAll(InputStream stream) throws IOException {
        StringBuffer buf = new StringBuffer();
        byte[] buffer = new byte[256];
        while(true) {
            int bytesRead = stream.read(buffer);
            if (bytesRead == -1) break;
            buf.append(new String(buffer, 0, bytesRead));
        }
        return buf.toString();
    }

    public static void serialize(Object o, OutputStream os) throws IOException {
    	ObjectOutputStream oos = new ObjectOutputStream(os);
    	oos.writeObject(o);
    	oos.flush();
    }

    
    public static byte[] serialize(Object o) {
    	try { 
	    	ByteArrayOutputStream out = new ByteArrayOutputStream();
	    	serialize(o, out);
	    	return out.toByteArray();
    	} catch (IOException e) { //this exception should not happen
    		throw new RuntimeException(e);
    	}
    }

    public static Object deserialize(InputStream is) throws IOException, ClassNotFoundException {
    	return new ObjectInputStream(is).readObject();
    }

   	public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
    	ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
    	return deserialize(bis);
    }
   	
   	public static String getStackTrace(Throwable t) {
        java.io.CharArrayWriter caw = new java.io.CharArrayWriter();
        t.printStackTrace(new java.io.PrintWriter(caw)); 
        return caw.toString();
   	}
}
