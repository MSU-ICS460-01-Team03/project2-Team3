package edu.metrostate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Receiver {
    public static void main(String[] args)
            throws IOException, ParameterException, ClassNotFoundException, InterruptedException {
        ReceiverParameter sp = new ReceiverParameter(args);
        byte[] data = new byte[1024];
        // System.out.println(sp);
        // DataPacket dp = new DataPacket((short) 0, (short) 0, 0, 0, new byte[100]);
        // System.out.println(dp);
        // AckPacket ack = new AckPacket((short) 23, 1);

        DatagramSocket sock = null;
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        FileOutputStream fos = null;
        try {
            sock = new DatagramSocket(sp.receiverPort);
            String fileStr = sp.filePath + "pandas.jpg";
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
                Thread.sleep(50);
            }

            System.out.println("success!");
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
