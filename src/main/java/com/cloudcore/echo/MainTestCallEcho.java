package com.cloudcore.echo;

import java.io.IOException;
import java.net.*;

public class MainTestCallEcho {

    public static void main(String[] args) throws IOException {
        System.out.println("Sending Echo test request.");

        DatagramSocket client = new DatagramSocket();
        InetAddress inetAddr = InetAddress.getLocalHost();
        SocketAddress socketAddr = new InetSocketAddress(inetAddr, 8888);

        byte[] msg = "echo test".getBytes();
        DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, socketAddr);
        client.send(sendPacket);

        client.close();
    }
}
