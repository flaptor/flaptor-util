package com.flaptor.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Assorted utilities for network handling.
 */
public class NetUtil {

    /**
     * Returns the list of local IP numbers.
     * @return an array list of String, each one representing an IP address local to this machine.
     */
    public static ArrayList<String> getLocalIPs () throws SocketException {
        ArrayList<String> ips = new ArrayList<String>();
        Enumeration<NetworkInterface> netcards = NetworkInterface.getNetworkInterfaces();
        while (netcards.hasMoreElements()) {
            Enumeration<InetAddress> inets = netcards.nextElement().getInetAddresses();
            while (inets.hasMoreElements()) {
                InetAddress inet = inets.nextElement();
                String addr = inet.getHostAddress();
                ips.add(addr);
            }
        }
        return ips;
    }

}

