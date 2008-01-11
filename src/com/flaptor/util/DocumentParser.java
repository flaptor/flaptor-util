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

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

/**
 * Utility class to parse xml using dom4j
 * without using DocumentHelper, which creates
 * a new reader every time. This class reuses
 * the same reader in a synchronized fashion,
 * which makes it more efficient in terms of memory
 * usage.
 */
public class DocumentParser {

	private static final Logger logger = Logger.getLogger(Execute.whoAmI());
	private SAXReader reader;

	public DocumentParser() {
		reader = new SAXReader();
	}

    /**
     * Takes an xml string and returns the DOM document that represents it.
     * @param s the xml string. Must not be null.
     * @return a DOM Document that represents the input xml string or null if the
     *    string is unparsable.
	 * @todo this only works if the system encoding is utf-8
     */
    public Document genDocument(final String s) {
        if (null == s) {
            logger.error("genDocument: received null as input.");
            throw new IllegalArgumentException();
        }
        Document doc;
        try {
            //we assume that SAXReader.read() is not thread-safe
            synchronized (reader) {
                //XXX TODO: getBytes should return a stream encoded in utf-8, to ensure it will work even if
                //that's not the default.
                doc = reader.read(new org.xml.sax.InputSource(new java.io.ByteArrayInputStream(s.getBytes())));
            }
        } catch (DocumentException e) {
            logger.debug("genDocument: cannot convert text to document.");
            return null;
        }
        return doc;
    }
}
        
