package com.HomeAuto.dashboard.backend.drivers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;

/**
 * Created by Rich on 23/09/2015.
 */
public class WeMo {

    public static void SwitchControl(String wemoAddr) {
        headers.'SOAPACTION' = "\"urn:Belkin:service:basicevent:1#SetBinaryState\""
        headers.'Content-Type' = "text/xml; charset=\"utf-8\""
        headers.'Accept' = ""
        String requestXml = "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
                "<s:Body>\n" +
                "<u:SetBinaryState  xmlns:u=\"urn:Belkin:service:basicevent:1\">\n" +
                "<BinaryState>1</BinaryState>\n" +
                "</u:SetBinaryState>\n" +
                "</s:Body>\n</s:Envelope>";
        try {
            URL url = new URL(wemoAddr);
            URLConnection con = url.openConnection();
            // specify that we will send output and accept input
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setConnectTimeout(20000);  // long timeout, but not infinite
            con.setReadTimeout(20000);
            con.setUseCaches(false);
            con.setDefaultUseCaches(false);
            // tell the web server what we are sending
            con.setRequestProperty("Content-Type", "text/xml");
            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
            writer.write(requestXml);
            writer.flush();
            writer.close();
            // reading the response
            InputStreamReader reader = new InputStreamReader(con.getInputStream());
            StringBuilder buf = new StringBuilder();
            char[] cbuf = new char[2048];
            int num;
            while (-1 != (num = reader.read(cbuf))) {
                buf.append(cbuf, 0, num);
            }
            String result = buf.toString();
            System.err.println("\nResponse from server after POST:\n" + result);
        } catch (Throwable t) {
            t.printStackTrace(System.out);
        }
    }

    public static void Discover(String wemoAddr){
        try {
            InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("239.255.255.250"), 1900);
            MulticastSocket socket = new MulticastSocket(null);
            socket.bind(new InetSocketAddress(InetAddress.getByName(wemoAddr), 1901));
            StringBuilder packet = new StringBuilder();
            packet.append( "M-SEARCH * HTTP/1.1\r\n" );
            packet.append( "HOST: 239.255.255.250:1900\r\n" );
            packet.append( "MAN: \"ssdp:discover\"\r\n" );
            packet.append( "MX: ").append( "5" ).append( "\r\n" );
            packet.append( "ST: " ).append( "ssdp:all" ).append( "\r\n" ).append( "\r\n" );
            //packet.append( "ST: " ).append( "urn:Belkin:device:controllee:1" ).append( "\r\n" ).append( "\r\n" )
            byte[] data = packet.toString().getBytes();
            socket.send(new DatagramPacket(data, data.length, socketAddress));
            socket.disconnect();
            socket.close();
        } catch (IOException e) {
            //throw e;
        } finally {

        }
    }
}
