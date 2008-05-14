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

    /**
     * same as getGeocode but only from cache (no google)
     */
    public Geocode getGeocodeCache(String place);
    /**
     * same as getGeocodingXmlCache but only from cache (no google)
     */
    public String getGeocodingXmlCache(final String place);
}
