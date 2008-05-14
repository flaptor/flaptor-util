package com.flaptor.util;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.apache.tools.ant.filters.StringInputStream;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * utilities for sax parsing
 * @author Martin Massera
 *
 */
public class SaxUtil {
    
    /**
     * parse an xml string with a given handler
     * @param xml
     * @param handler
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public static void parse(String xml, DefaultHandler handler) throws SAXException, IOException, ParserConfigurationException {
        SAXParserFactory.newInstance().newSAXParser().parse(new StringInputStream(xml), handler);
    }

}
