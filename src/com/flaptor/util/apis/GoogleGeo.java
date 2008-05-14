package com.flaptor.util.apis;

/**
 * Interface for Geocoding queries 
 * 
 * @author Martin Massera
 */
public interface GoogleGeo {
    
    /**
     * get from cache or google api the geographic information
     * 
     * blocking wait to enforce the request limit 
     * @param place
     * @return
     */
    public Geocode getGeocode(String place);
    
    /**
     * get from cache or google api the geocoding xml
     * 
     * blocking wait to enforce the request limit 
     * @param place
     * @return
     */
    public String getGeocodingXml(final String place);
}
