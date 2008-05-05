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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

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
    /**
     * fully reads a stream
     * @return a string with all the contents of the stream 
     * @throws IOException 
     */
    public static byte[] readAllBinary(InputStream stream) throws IOException {
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        byte[] buffer = new byte[256];
        while(true) {
            int bytesRead = stream.read(buffer);
            if (bytesRead == -1) break;
            o.write(buffer);
        }
        return o.toByteArray();
    }

    /**
     * reads the last n bytes of a file
     * @param stream
     * @param numBytes
     * @return
     * @throws IOException
     */
    public static byte[] tail(File file, long numBytes) throws IOException {
        RandomAccessFile raFile = null;
        try {
            raFile = new RandomAccessFile(file, "r");
            long position = raFile.length() - numBytes;
            if (position < 0) position = 0;
            raFile.seek(position);
            byte[] ret = new byte[(int)(raFile.length() - position)];
            raFile.read(ret);
            return ret;
        } finally {
            if (raFile != null) raFile.close();
        }
        
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
   	
   	/**
     * reads all non empty lines 
     * @param toLowerCase if true makes every line lowercase
   	 * @return a list of strings with a line per element
   	 * @throws IOException 
   	 */
   	public static List<String> readLines(boolean toLowerCase, boolean trim, boolean emptyLines, Reader reader) throws IOException {
   	    List<String> ret = new ArrayList<String>();
   	    BufferedReader bufferedReader = new BufferedReader(reader);
   	    while (true) {
   	        String line = bufferedReader.readLine();
   	        if (line == null) return ret;
   	        if (toLowerCase) line = line.toLowerCase();
   	        if (trim) line = line.trim();
   	        if (!emptyLines && line.length() == 0) continue;
   	        ret.add(line);
   	    }
   	}
}
