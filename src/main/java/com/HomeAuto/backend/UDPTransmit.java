package com.HomeAuto.backend;

import java.io.*;
import java.net.*;
import javax.xml.bind.DatatypeConverter;

public class UDPTransmit {

    public static byte[] sendData = {0x00, 0x00};

    public static void udpSend(String netHost, int netPort){
        try {
            // Get the internet address of the specified host
            InetAddress address = InetAddress.getByName(netHost);

            // Initialize a datagram packet with data and address
            DatagramPacket packet = new DatagramPacket(sendData, sendData.length,
                    address, netPort);

            // Create a datagram socket, send the packet through it, close it.
            DatagramSocket dsocket = new DatagramSocket();
            dsocket.send(packet);
            dsocket.close();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private static String stringToByteString(String s){
        String output1 = s.substring(0,4);
        String output2 = s.substring(4, 8);
        String output3 = s.substring(8,12);
        String output = output1 + "@" + output2 + "@" + output3;
        System.out.println(output);
        return output;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
