package com.flaptor.util.apis;

import java.rmi.Remote;

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
    public ApiCache(int port, String geocacheDir, String geocacheKey) {
        super(port);
        addHandler("googleGeo", RmiCodeGeneration.remoteHandler("googleGeo", new Class[] {GoogleGeo.class}, new GoogleGeoImpl(geocacheDir,geocacheKey)));
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("usage: port geocacheDir googleMapsKey");
            System.exit(-1);
        }
        int port = Integer.parseInt(args[0]);
        logger.info("starting apicache on port " + port);
        new ApiCache(port, args[1], args[2]).start();
    }
    
    public static GoogleGeo getGoogleGeo(String host, int port) {
        return (GoogleGeo)RmiCodeGeneration.reconnectableStub("googleGeo", new Class[]{GoogleGeo.class}, "googleGeo", host, port, new AlwaysRetryPolicy());
    }
}
