package com.flaptor.util.apis;

import org.apache.log4j.Logger;

import com.flaptor.util.Execute;
import com.flaptor.util.remote.AlwaysRetryPolicy;
import com.flaptor.util.remote.RmiCodeGeneration;
import com.flaptor.util.remote.RmiServer;

/**
 * Rmi server for accessing API caching services
 * @author Martin Massera
 */
public class ApiCache extends RmiServer {
    private static final Logger logger = Logger.getLogger(Execute.whoAmI());
    public ApiCache(int port) {
        super(port);
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("usage: port geocacheDir googleMapsKey");
            System.exit(-1);
        }
        int port = Integer.parseInt(args[0]);
        ApiCache apiCache = new ApiCache(port);
        apiCache.addHandler("googleGeo", RmiCodeGeneration.remoteHandler("googleGeo", new Class[] {GoogleGeo.class}, new GoogleGeoImpl(args[1],args[2])));
        apiCache.start();
        logger.info("starting apicache on port " + port);
    }
    
    public static GoogleGeo getGoogleGeo(String host, int port) {
        return (GoogleGeo)RmiCodeGeneration.reconnectableStub("googleGeo", new Class[]{GoogleGeo.class}, "googleGeo", host, port, new AlwaysRetryPolicy());
    }
}
