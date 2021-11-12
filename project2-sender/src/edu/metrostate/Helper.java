package edu.metrostate;

/**
 * @author 
 *
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Helper extends Print {
    public static List<DataPacket> makePacketList() throws IOException {
        InputParameter sp = InputParameter.instance();
        String fileStr = sp.filePath + sp.fileName;
        File myFile = null;
        FileInputStream fis = null;
        try {
            myFile = new File(fileStr);
            fis = new FileInputStream(myFile);
            List<DataPacket> sendPacks = new ArrayList<>();
            byte[] data = new byte[sp.packetSize];
            int i = 1;
            int count = 0;
            while ((count = fis.read(data)) != -1) {
                sendPacks.add(new DataPacket((short) (count + 12), i, i, data.clone()));
                i++;
            }
            sp.totalPacket = sendPacks.size();
            return sendPacks;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null)
                fis.close();
        }
        return null;
    }

    public static void generateRandomErrDrop(List<Integer> errs, List<Integer> drops) {
        InputParameter sp = InputParameter.instance();
        Random random = new Random();
        int errDrop = (int) ((sp.totalPacket / 2) * sp.percentError);
        for (int i = 0; i < errDrop; i++) {
            errs.add(random.nextInt(sp.totalPacket - 2) + 2);
            drops.add(random.nextInt(sp.totalPacket - 2) + 2);
        }
    }

    public static void sendHeadPacket(int sendPacksSize, DatagramSocket sock, ByteArrayOutputStream bos,
            ObjectOutputStream oos) throws IOException {
        InputParameter sp = InputParameter.instance();
        Map<String, Integer> headPacket = new HashMap<>();
        headPacket.put(sp.fileName, sendPacksSize);
        bos = new ByteArrayOutputStream();
        oos = new ObjectOutputStream(bos);
        oos.writeObject(headPacket);
        oos.flush();
        byte[] sendData = bos.toByteArray();
        DatagramPacket sendPack = new DatagramPacket(sendData, sendData.length,
                InetAddress.getByName(sp.receiverIpAddress), sp.receiverPort);
        sock.send(sendPack);
        System.out.println("Start Sending file name: " + sp.fileName + ", with total: " + sendPacksSize + " packets");
    }

    public static void sendDatagramPacket(DatagramSocket sock, DataPacket dp, ByteArrayOutputStream bos,
            ObjectOutputStream oos) throws IOException {
        InputParameter sp = InputParameter.instance();
        bos = new ByteArrayOutputStream();
        oos = new ObjectOutputStream(bos);
        oos.writeObject(dp);
        oos.flush();
        byte[] sendData = bos.toByteArray();
        DatagramPacket sendPack = new DatagramPacket(sendData, sendData.length,
                InetAddress.getByName(sp.receiverIpAddress), sp.receiverPort);
        sock.send(sendPack);

    }

    public static AckPacket receiveAck(DatagramSocket sock, ByteArrayInputStream bais, ObjectInputStream ois)
            throws IOException, ClassNotFoundException {
        InputParameter sp = InputParameter.instance();
        byte[] data = new byte[sp.packetSize];
        DatagramPacket inPack = new DatagramPacket(data, data.length);
        try {

            sock.receive(inPack);
        } catch (SocketTimeoutException ste) {

            return null;
        }
        byte[] recData = inPack.getData();
        bais = new ByteArrayInputStream(recData);
        ois = new ObjectInputStream(bais);
        AckPacket ack = (AckPacket) ois.readObject();
        return ack;
    }

    public static void closeAll(DatagramSocket sock, ByteArrayOutputStream bos, ObjectOutputStream oos,
            ByteArrayInputStream bais) throws IOException {
        if (sock != null)
            sock.close();
        if (bos != null)
            bos.close();
        if (oos != null)
            oos.close();
        if (bais != null)
            bais.close();
    }
}

