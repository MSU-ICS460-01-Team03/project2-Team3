package edu.metrostate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Sender {
    public static void main(String[] args) throws IOException, ParameterException, ClassNotFoundException {
        SenderParameter sp = new SenderParameter(args);
        // System.out.println(sp);
        DataPacket dp = new DataPacket((short) 64, 5, 3, new byte[500], "", "");
        // System.out.println(dp);
        // System.out.println(dp.isError("", ""));
        // AckPacket ack = new AckPacket((short) 0, (short) 0, 1);
        // System.out.println(ap);
        // byte[] sendData = new byte[500];
        DatagramSocket sock = null;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        byte[] data = new byte[1024];
        try {
            sock = new DatagramSocket(sp.senderPort);

            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(dp);
            byte[] sendData = bos.toByteArray();
            InetAddress address = InetAddress.getByName(sp.receiverIpAddress);
            System.out.println(sendData.length);
            DatagramPacket sendPack = new DatagramPacket(sendData, sendData.length, address, sp.receiverPort);
            sock.send(sendPack);

            DatagramPacket inPack = new DatagramPacket(data, data.length);
            sock.receive(inPack);
            byte[] recData = inPack.getData();
            bis = new ByteArrayInputStream(recData);
            ois = new ObjectInputStream(bis);
            AckPacket ack = (AckPacket) ois.readObject();
            System.out.println(ack);
            System.out.println(ack.isError("", ""));

        } finally {
            if (sock != null)
                sock.close();
            if (bos != null)
                bos.close();
            if (oos != null)
                oos.close();
            // if (bis != null)
            // bis.close();

        }

    }
}
