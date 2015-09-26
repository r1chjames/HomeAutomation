package com.HomeAuto.dashboard.backend.drivers;

public class LimitlessLED {


    public static void lightControl(int group, String command){
        String host = "bridge-limitlessled";
        int port = 8899;

        if (command.equals("on")) {
            if (group == 1){
                UDPTransmit.sendData[0] = (byte)0x45;
            }
            if (group == 2){
                UDPTransmit.sendData[0] = (byte)0x47;
            }
            else{
                UDPTransmit.sendData[0] = (byte)0x42;
            }
        }
        if (command.equals("off")){
            if (group == 1){
                UDPTransmit.sendData[0] = (byte)0x46;
            }
            if (group == 2){
                UDPTransmit.sendData[0] = (byte)0x48;
            }
            else{
                UDPTransmit.sendData[0] = (byte)0x41;
            }
        }
        if (command.equals("white")){
            lightControl(group,"on");
            UDPTransmit.sendData[0] = (byte)0xC2;
        }
        if (command.equals("green")){
            lightControl(group,"on");
            UDPTransmit.sendData[0] = (byte)0x40;
            UDPTransmit.sendData[1] = (byte)0x60;
        }
        if (command.equals("blue")){
            lightControl(group,"on");
            UDPTransmit.sendData[0] = (byte)0x40;
            UDPTransmit.sendData[1] = (byte)0x20;
        }

        UDPTransmit.udpSend(host, port);
    }
}
