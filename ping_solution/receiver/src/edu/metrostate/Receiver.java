package edu.metrostate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Receiver {
    public static void main(String[] args) throws IOException, ParameterException, ClassNotFoundException {
        ReceiverParameter sp = new ReceiverParameter(args);

        // System.out.println(sp);
        // DataPacket dp = new DataPacket((short) 0, (short) 0, 0, 0, new byte[100]);
        // System.out.println(dp);
        AckPacket ack = new AckPacket((short) 23, 1, "", "");
        // System.out.println(ap);

        DatagramSocket sock = null;
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {
            sock = new DatagramSocket(sp.receiverPort);
            byte[] data = new byte[1024];
            while (true) {
                DatagramPacket inPack = new DatagramPacket(data, data.length);
                sock.receive(inPack);
                byte[] recData = inPack.getData();
                System.out.println(recData.length);
                bis = new ByteArrayInputStream(recData);
                ois = new ObjectInputStream(bis);
                // DataPacket dp = null;

                DataPacket dp = (DataPacket) ois.readObject();
                System.out.println(dp.isError("", ""));
                System.out.println(dp);

                bos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(bos);
                oos.writeObject(ack);
                byte[] sendData = bos.toByteArray();
                InetAddress address = inPack.getAddress();
                DatagramPacket outPack = new DatagramPacket(sendData, sendData.length, address, inPack.getPort());
                sock.send(outPack);
            }
        } finally {
            if (sock != null) {

                sock.close();
            } else {
                sock = null;
            }
        }

    }
}
