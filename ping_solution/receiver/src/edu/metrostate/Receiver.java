package edu.metrostate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Receiver {
    public static void main(String[] args)
            throws IOException, ParameterException, ClassNotFoundException, InterruptedException {
        ReceiverParameter sp = new ReceiverParameter(args);
        byte[] data = new byte[1024];
        // System.out.println(sp);
        DatagramSocket sock = null;
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        FileOutputStream fos = null;
        InetAddress address = InetAddress.getByName(sp.senderIpAddress);
        try {
            sock = new DatagramSocket(sp.receiverPort);
            String fileStr = sp.filePath + sp.fileName;
            fos = new FileOutputStream(fileStr);

            boolean flag = true;

            while (flag) {

                DatagramPacket inPack = new DatagramPacket(data, data.length);
                sock.receive(inPack);

                byte[] recData = inPack.getData();
                bais = new ByteArrayInputStream(recData);
                ois = new ObjectInputStream(bais);
                DataPacket dp = (DataPacket) ois.readObject();

                if (dp.data.length != 0) {
                    fos.write(dp.data, 0, dp.len - 12);
                    fos.flush();

                } else {
                    flag = false;
                }

                AckPacket ack = new AckPacket((short) 8, dp.seqno);
                bos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(bos);
                oos.writeObject(ack);
                oos.flush();
                byte[] sendData = bos.toByteArray();
                DatagramPacket sendPack = new DatagramPacket(sendData, sendData.length, address, sp.senderPort);
                sock.send(sendPack);
                Thread.sleep(25);
            }

            System.out.println("Received success!");
        } finally {

            if (fos != null)
                fos.close();
            if (bais != null)
                bais.close();
            if (ois != null)
                ois.close();
            if (bos != null)
                bos.close();
            if (oos != null)
                oos.close();
            if (sock != null) {
                sock.close();
            } else {
                sock = null;
            }
        }

    }
}
