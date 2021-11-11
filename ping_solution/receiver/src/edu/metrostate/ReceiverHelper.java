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
// import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ReceiverHelper {
    public static void receiveFirstData(DatagramSocket sock, ByteArrayInputStream bais, ObjectInputStream ois)
            throws IOException, ClassNotFoundException {
        ReceiverParameter sp = ReceiverParameter.instance();
        byte[] data = new byte[1024];
        DatagramPacket inPack = new DatagramPacket(data, data.length);
        sock.receive(inPack);
        byte[] recData = inPack.getData();
        bais = new ByteArrayInputStream(recData);
        ois = new ObjectInputStream(bais);
        Map<String, Integer> headPack = (Map<String, Integer>) ois.readObject();
        for (Map.Entry<String, Integer> e : headPack.entrySet()) {
            sp.fileName = e.getKey();
            sp.totalPacket = e.getValue();
        }
        System.out.println("Receive file name: " + sp.fileName + ", with total: " + sp.totalPacket + " packets");
    }

    public static void generateRandomErrDrop(List<Integer> errs, List<Integer> drops) {
        ReceiverParameter sp = ReceiverParameter.instance();
        System.out.println("null?" + sp);
        Random random = new Random();
        int errDrop = (int) ((sp.totalPacket / 2) * sp.percentError);
        for (int i = 0; i < errDrop; i++) {
            errs.add(random.nextInt(sp.totalPacket - 1) + 1);
            drops.add(random.nextInt(sp.totalPacket - 1) + 1);
        }

    }

    public static DataPacket receiveDatagramPacket(DatagramSocket sock, ByteArrayInputStream bais,
            ObjectInputStream ois) throws IOException, ClassNotFoundException {
        byte[] data = new byte[1024];
        DatagramPacket inPack = new DatagramPacket(data, data.length);
        sock.receive(inPack);
        byte[] recData = inPack.getData();
        bais = new ByteArrayInputStream(recData);
        ois = new ObjectInputStream(bais);
        DataPacket dp = (DataPacket) ois.readObject();
        return dp;
    }

    public static void extractAndDeliver(FileOutputStream fos, DataPacket dp) throws IOException {
        fos.write(dp.data, 0, dp.len - 12);
        fos.flush();

    }

    public static void sendAck(DatagramSocket sock, ByteArrayOutputStream bos, ObjectOutputStream oos, int seqno,
            int cksum) throws IOException {
        ReceiverParameter sp = ReceiverParameter.instance();
        AckPacket ack = new AckPacket((short) 8, seqno);
        ack.cksum = (short) cksum;
        bos = new ByteArrayOutputStream();
        oos = new ObjectOutputStream(bos);
        oos.writeObject(ack);
        oos.flush();
        byte[] sendData = bos.toByteArray();
        InetAddress address = InetAddress.getByName(sp.senderIpAddress);
        DatagramPacket sendPack = new DatagramPacket(sendData, sendData.length, address, sp.senderPort);
        sock.send(sendPack);
        PrintEachPacket.ackSentPrint(ack.ackno, PrintEachPacket.SENT);
    }

    public static void closeAll(DatagramSocket sock, ByteArrayOutputStream bos, ObjectOutputStream oos,
            FileOutputStream fos, ByteArrayInputStream bais, ObjectInputStream ois) throws IOException {
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
