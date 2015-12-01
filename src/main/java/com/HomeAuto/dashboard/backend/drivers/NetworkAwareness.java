package com.HomeAuto.dashboard.backend.drivers;

import java.io.IOException;
import java.net.InetAddress;

public class NetworkAwareness {


    public static void ListDevices(String ipSubnet) throws IOException {
        int timeout=1000;
        for (int i=1;i<255;i++){
            String host=ipSubnet + "." + i;
            try {
                if (InetAddress.getByName(host).isReachable(timeout)) {
                    System.out.println(host + " is reachable");
                }
            }
            catch (IOException e) {
                System.out.println(e);
            }
        }
    }


    public static boolean PingDevice(String ipAddr) throws IOException {
        int timeout=10000;
        boolean retVal = false;
            try {
                if (InetAddress.getByName(ipAddr).isReachable(timeout)) {
                System.out.println(ipAddr + " is reachable");
                    retVal = true;
                }
            }
            catch (IOException e) {
                System.out.println(e);
            }
        return retVal;
    }
}

