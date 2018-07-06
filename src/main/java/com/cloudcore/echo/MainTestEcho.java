package com.cloudcore.echo;

/*
  Copyright (c) 2018 Ben Ward, 07/06/18

  This work is licensed under the terms of the MIT license.
  For a copy, see <https://opensource.org/licenses/MIT>.
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

public class MainTestEcho {

    public static void main(String[] args) throws IOException {
        boolean restart = false;
        CommandInterpreter.initialize();

        DatagramSocket server = new DatagramSocket(8888);
        DatagramPacket recvPacket = new DatagramPacket(new byte[255], 255);

        System.out.println("Waiting for commands...");

        while (!restart)
        {
            server.receive(recvPacket);

            byte[] receiveMsg = Arrays.copyOfRange(recvPacket.getData(),
                    recvPacket.getOffset(),
                    recvPacket.getOffset() + recvPacket.getLength());

            System.out.println("Handing at client "
                    + recvPacket.getAddress().getHostName() + " ip "
                    + recvPacket.getAddress().getHostAddress());

            String msg = new String(receiveMsg);
            System.out.println("Server Receive Data:" + msg);

            if (msg.equalsIgnoreCase("echo test"))
            CommandInterpreter.testRaidaEcho();
        }
    }
}
