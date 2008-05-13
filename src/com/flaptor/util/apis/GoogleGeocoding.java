package com.flaptor.util.apis;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.apache.tools.ant.filters.StringInputStream;
import org.xml.sax.SAXException;

import com.flaptor.util.Execute;
import com.flaptor.util.IOUtil;
import com.flaptor.util.StringUtil;
import com.flaptor.util.ThreadUtil;
import com.flaptor.util.cache.MemFileCache;
import com.flaptor.util.xml.SaxStackHandler;

public class GoogleGeocoding {
    
    private static final Logger logger = Logger.getLogger(Execute.whoAmI());

	private long lastCall = System.currentTimeMillis();
	
	private MemFileCache<String> geoCache;
	private String key;

	public GoogleGeocoding(String cacheDir, String key) {
		geoCache = new MemFileCache<String>(50000, cacheDir);
	}
	
	public MemFileCache<String> getCache() {
		return geoCache;
	}
	
	public String getLocation(String place) {
		String ret = geoCache.get(place);
		if (ret == null) {
			synchronized (this) {
				//enforce the 1 request/1.750 sec limit
				while (System.currentTimeMillis() - lastCall < 1750) {
					ThreadUtil.sleep(50);
				}
				HttpClient client = new HttpClient();
				String url = "http://maps.google.com/maps/geo?q="+StringUtil.urlEncode(place) + "&output=xml&key=" + key; 
				GetMethod get = new GetMethod(url);
				try {
					logger.info("retrieving " + place);
					int status = client.executeMethod(get);
					if (status !=  200) throw new Exception("http request failed - status " + status);
					ret = IOUtil.readAll(get.getResponseBodyAsStream());
					get.releaseConnection();
					lastCall = System.currentTimeMillis();
				} catch (Exception e) {
					logger.warn(e,e);
					return null;
				}
				geoCache.put(place, ret);
			}
		}
		return ret;
	}
}
