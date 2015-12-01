package com.HomeAuto.dashboard.backend.drivers;

/**
 * Created by Rich on 23/09/2015.
 */

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.*;

public class SonyBravia {

    public static String GetStatus(String addr) {
        String status = null;
        String strresponse = null;
        try{
            String body = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
                    "  <s:Body>\n" +
                    "    <u:GetBinaryState xmlns:u=\"urn:Belkin:service:basicevent:1\"></u:GetBinaryState>\n" +
                    "  </s:Body>\n" +
                    "</s:Envelope>";
            String soapaction = "\"urn:Belkin:service:basicevent:1#GetBinaryState\"";
            HttpPost httppost = new HttpPost(addr);

            // Request parameters and other properties.
            StringEntity stringentity = new StringEntity(body);
            httppost.setEntity(stringentity);
            httppost.addHeader("Accept", "");
            httppost.addHeader("Content-Type","text/xml; charset=\"UTF-8\"");
            httppost.addHeader("SOAPAction", soapaction);
            //Execute and get the response.
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
            strresponse = EntityUtils.toString(entity);
            }
            if (strresponse.contains("<BinaryState>1</BinaryState>")){
                status = "On";
            }
            else if (strresponse.contains("<BinaryState>0</BinaryState>")){
                status = "Off";
            }
        }
        catch (IOException | URISyntaxException | HttpException e){ System.out.println(e); }

        return status;
    }

public static void SetStatus(String addr, String command) throws IOException { //TODO This doesn't work.
    //Check https://github.com/KHerron/SonyAPILib
    //http://forum.xda-developers.com/showthread.php?t=2091564&page=3
    //http://mendelonline.be/sony/sony.txt
    //http://www.openremote.org/display/forums/Sony+TV+HTTP+control

    String IIRC = null;
    String strresponse = null;
    if (command == "On"){
        IIRC = "0";
    }
    else if (command == "Off"){
        IIRC = "AAAAAQAAAAEAAAAvAw==";
    }
    System.out.println(command);
    try{
        String body ="<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
                "   <s:Body>\n" +
                "       <u:X_SendIRCC xmlns:u=\"urn:schemas-sony-com:service:IRCC:1\">\n" +
                "           <IRCCCode>" + IIRC + "</IRCCCode>\n" +
                "       </u:X_SendIRCC>\n" +
                "   </s:Body>\n" +
                "</s:Envelope>";
        String soapaction = "\"urn:schemas-sony-com:service:IRCC:1#X_SendIRCC\"";
        HttpPost httppost = new HttpPost(addr);

        // Request parameters and other properties.
        StringEntity stringentity = new StringEntity(body);
        httppost.setEntity(stringentity);
        httppost.addHeader("Accept", "");
        httppost.addHeader("Content-Type","text/xml; charset=\"UTF-8\"");
        httppost.addHeader("SOAPAction", soapaction);
        //Execute and get the response.
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            strresponse = EntityUtils.toString(entity);
        }
    }
    catch (IOException | URISyntaxException | HttpException e){ System.out.println(e); }
}

    public static void Discover(String addr){
            try {
            InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("239.255.255.250"), 1900);
            MulticastSocket socket = new MulticastSocket(null);
            socket.bind(new InetSocketAddress(InetAddress.getByName(addr), 1901));
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
