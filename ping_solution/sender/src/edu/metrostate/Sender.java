package edu.metrostate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import javax.xml.crypto.Data;

public class Sender {

    public static void main(String[] args) throws IOException, ParameterException, ClassNotFoundException,
            FileNotFoundException, InterruptedException {
        SenderParameter sp = new SenderParameter(args);
        // System.out.println(sp);
        // DataPacket dp;
        int count = 0;
        InetAddress address = InetAddress.getByName(sp.receiverIpAddress);
        // System.out.println(dp);
        // System.out.println(dp.isError("", ""));
        // AckPacket ack = new AckPacket((short) 0, (short) 0, 1);
        // System.out.println(ap);
        // byte[] sendData = new byte[500];
        DatagramSocket sock = null;
        // DatagramPacket sendPack;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        byte[] data = new byte[sp.packetSize];
        FileInputStream fis = null;
        File myFile = null;
        List<DataPacket> sendPacks = new ArrayList<>();
        try {

            sock = new DatagramSocket(sp.senderPort);
            String fileStr = sp.filePath + sp.fileName;

            myFile = new File(fileStr);

            fis = new FileInputStream(myFile);

            int i = 0;

            while ((count = fis.read(data)) != -1) {

                // DataPacket dp = ;

                sendPacks.add(new DataPacket((short) (count + 12), i, i, data.clone()));
                i++;
            }
            for (DataPacket pack : sendPacks) {
                bos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(bos);
                oos.writeObject(pack);
                oos.flush();
                byte[] sendData = bos.toByteArray();
                DatagramPacket sendPack = new DatagramPacket(sendData, sendData.length, address, sp.receiverPort);
                sock.send(sendPack);
                Thread.sleep(50);
            }

            DataPacket dp = new DataPacket((short) 0, 0, 0, new byte[0]);
            System.out.println(dp);
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(dp);
            oos.flush();
            byte[] ata = bos.toByteArray();
            DatagramPacket sendPack2 = new DatagramPacket(ata, ata.length, address, sp.receiverPort);
            sock.send(sendPack2);

            // DatagramPacket inPack = new DatagramPacket(data, data.length);
            // sock.receive(inPack);
            // byte[] recData = inPack.getData();
            // bais = new ByteArrayInputStream(recData);
            // ois = new ObjectInputStream(bais);
            // AckPacket ack = (AckPacket) ois.readObject();
            // System.out.println(ack);
            // System.out.println(ack.isError());

        } finally {
            if (sock != null)
                sock.close();
            if (bos != null)
                bos.close();
            if (oos != null)
                oos.close();
            if (fis != null)
                fis.close();
            if (bais != null)
                bais.close();

        }

    }
}
