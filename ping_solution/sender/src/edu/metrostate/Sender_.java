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
import java.util.Iterator;
import java.util.List;

public class Sender_ {

    public static void main(String[] args) throws IOException, ParameterException, ClassNotFoundException,
            FileNotFoundException, InterruptedException {
        SenderParameter sp = new SenderParameter(args);
        int count = 0;
        InetAddress address = InetAddress.getByName(sp.receiverIpAddress);
        DatagramSocket sock = null;
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

            int i = 1;

            while ((count = fis.read(data)) != -1) {
                sendPacks.add(new DataPacket((short) (count + 12), i, i, data.clone()));
                i++;
            }
            Iterator<DataPacket> iter = sendPacks.iterator();
            DataPacket dp = iter.next();
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(dp);
            oos.flush();
            byte[] firstSendData = bos.toByteArray();
            DatagramPacket firstSendPack = new DatagramPacket(firstSendData, firstSendData.length, address,
                    sp.receiverPort);
            sock.send(firstSendPack);
            while (iter.hasNext()) {
                DatagramPacket inPack = new DatagramPacket(data, data.length);
                sock.receive(inPack);

                byte[] recData = inPack.getData();
                bais = new ByteArrayInputStream(recData);
                ois = new ObjectInputStream(bais);
                AckPacket ack = (AckPacket) ois.readObject();
                if (!ack.isError()) {
                    dp = iter.next();
                    bos = new ByteArrayOutputStream();
                    oos = new ObjectOutputStream(bos);
                    oos.writeObject(dp);
                    oos.flush();
                    byte[] sendData = bos.toByteArray();
                    DatagramPacket sendPack = new DatagramPacket(sendData, sendData.length, address, sp.receiverPort);
                    sock.send(sendPack);

                }
                Thread.sleep(25);
            }

            dp = new DataPacket((short) 0, 0, 0, new byte[0]);
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(dp);
            oos.flush();
            byte[] ata = bos.toByteArray();
            DatagramPacket sendPack2 = new DatagramPacket(ata, ata.length, address, sp.receiverPort);
            sock.send(sendPack2);
            System.out.println("Sent! success.");

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
