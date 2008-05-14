package com.flaptor.util.apis;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.apache.tools.ant.filters.StringInputStream;
import org.xml.sax.SAXException;

import com.flaptor.util.CollectionsUtil;
import com.flaptor.util.Execute;
import com.flaptor.util.IOUtil;
import com.flaptor.util.Pair;
import com.flaptor.util.StringUtil;
import com.flaptor.util.ThreadUtil;
import com.flaptor.util.cache.MemFileCache;
import com.flaptor.util.xml.SaxStackHandler;

/**
 * 
 * Cache for google geocoding api, that enforces the request limits
 * 
 * @author Martin Massera
 *
 */
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
	
    /**
     * get from cache or google api the geographic information
     * 
     * blocking wait to enforce the request limit 
     * @param place
     * @return
     */
	public Geocode getGeocode(String place) {
	    String xml = getGeocodingXml(place);
	    if (xml != null) return parse(xml);
	    else return null; 
	}
	
	/**
	 * get from cache or google api the geocoding xml
	 * 
	 * blocking wait to enforce the request limit 
	 * @param place
	 * @return
	 */
	public String getGeocodingXml(String place) {
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
	
	/**
	 * parses XML and gets the locality and country
	 * works only when only one place matches the query
	 * 
	 * @param xml
	 * @return
	 */
	static public Geocode parse(String xml) {
	    final boolean[] valid = new boolean[] {true};
        final Geocode placeInfo = new Geocode(null, null, null);
        try {
            SAXParserFactory.newInstance().newSAXParser().parse(new StringInputStream(xml), new SaxStackHandler() {
                public void endElement(String uri, String localName, String name, String textContent) throws SAXException {
                    if (name.equals("code")){
                        if (!textContent.equals("200")) valid[0] = false;
                    }
                    if (name.equals("CountryNameCode")) {
                        if (placeInfo.getCountry() != null) valid[0] = false;
                        placeInfo.setCountry(textContent);
                    }
                    if (name.equals("LocalityName")) placeInfo.setLocality(textContent);
                    if (name.equals("coordinates")) {
                        String[] coords = textContent.split(",");
                        placeInfo.setLatLong(new double[] {Double.parseDouble(coords[1]),Double.parseDouble(coords[0])});
                    }
                }
            });
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
        if (valid[0]) return placeInfo;
        else return null; 
	}
}
