package com.HomeAuto.dashboard.backend;

import java.io.IOException;
import java.net.*;

/**
 * Created by Rich on 23/09/2015.
 */
public class WeMo {

    public static void Switch() throws IOException{
        InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("239.255.255.250"), 1900);
        MulticastSocket socket = new MulticastSocket(null);
        try {
            socket.bind(new InetSocketAddress("192.168.0.26", 1901));
            StringBuilder packet = new StringBuilder();
            packet.append( "M-SEARCH * HTTP/1.1\r\n" );
            packet.append( "HOST: 239.255.255.250:1900\r\n" );
            packet.append( "MAN: \"ssdp:discover\"\r\n" );
            packet.append( "MX: ").append( "5" ).append( "\r\n" );
            packet.append( "ST: " ).append( "ssdp:all" ).append( "\r\n" ).append( "\r\n" );
            //packet.append( "ST: " ).append( "urn:Belkin:device:controllee:1" ).append( "\r\n" ).append( "\r\n" )
            byte[] data = packet.toString().getBytes();
            socket.send(new DatagramPacket(data, data.length, socketAddress));
        } catch (IOException e) {
            //throw e;
        } finally {
            socket.disconnect();
            socket.close();
        }
    }
}
