package com.flaptor.util.apis;

import com.flaptor.util.remote.RmiCodeGeneration;
import com.flaptor.util.remote.RmiServer;

/**
 * Rmi server for accessing API caching services
 * @author Martin Massera
 */
public class ApiCache extends RmiServer {

    public ApiCache(int port) {
        super(port);
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("usage: port geocacheDir googleMapsKey");
            System.exit(-1);
        }
        ApiCache apiCache = new ApiCache(Integer.parseInt(args[0]));
        RmiCodeGeneration.remoteHandler("georemote", new Class[] {GoogleGeo.class}, new GoogleGeoImpl(args[1],args[2]));
        apiCache.start();
    }
}
