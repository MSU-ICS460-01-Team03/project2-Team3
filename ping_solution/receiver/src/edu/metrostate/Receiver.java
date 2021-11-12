package edu.metrostate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

public class Receiver extends Helper {
    public static void main(String[] args)
            throws IOException, InputException, ClassNotFoundException, InterruptedException {
        InputParameter sp = InputParameter.instance();
        sp.getArgs(args);

        DatagramSocket sock = null;
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        FileOutputStream fos = null;

        try {
            sock = new DatagramSocket(sp.receiverPort);
            System.out.println(">>>>>>>>>>>>>>>>>\nWaiting for receiving a file.....");
            receiveFirstData(sock, bais, ois);
            List<Integer> errs = new ArrayList<Integer>();
            List<Integer> drops = new ArrayList<Integer>();
            generateRandomErrDrop(errs, drops);
            fos = new FileOutputStream(sp.filePath + sp.fileName);
            boolean flag = true;
            int seq = 1;
            while (flag) {
                DataPacket dp = receiveDatagramPacket(sock, bais, ois);
                AckPacket ack = new AckPacket((short) 8, dp.seqno);
                if (dp.isError()) {
                    datagramReceivedPrint(RECV, dp.seqno, CRPT);
                } else if (dp.data.length == 0) {
                    flag = false;
                } else if (seq != dp.seqno) {
                    datagramReceivedPrint(DUPL, dp.seqno, NOT_SEQ);
                    sendAck(sock, bos, oos, ack);
                } else {
                    seq++;
                    extractAndDeliver(fos, dp);
                    datagramReceivedPrint(RECV, dp.seqno, RECV);
                    if (errs.contains(dp.seqno)) {
                        errs.remove(errs.indexOf(dp.seqno));
                        ack.cksum = 1;
                        sendAck(sock, bos, oos, ack);
                        ackSentPrint(ack.ackno, CRPT);
                    } else if (drops.contains(dp.seqno)) {
                        drops.remove(drops.indexOf(dp.seqno));
                        ackSentPrint(ack.ackno, DROP);
                        ack.ackno = ack.ackno - 1;
                        sendAck(sock, bos, oos, ack);
                        continue;
                    } else {
                        sendAck(sock, bos, oos, ack);
                        ackSentPrint(ack.ackno, SENT);
                    }
                }
            }
            System.out.println("Received success!");
        } finally {
            closeAll(sock, bos, oos, fos, bais, ois);
            System.out.println("Receiver Program Terminated!");
        }

    }
}
