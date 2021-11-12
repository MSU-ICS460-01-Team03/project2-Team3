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

public class Receiver {
    public static void main(String[] args)
            throws IOException, ParameterException, ClassNotFoundException, InterruptedException {
        ReceiverParameter sp = ReceiverParameter.instance();
        sp.getArgs(args);

        DatagramSocket sock = null;
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        FileOutputStream fos = null;

        try {
            sock = new DatagramSocket(sp.receiverPort);
            ReceiverHelper.receiveFirstData(sock, bais, ois);
            List<Integer> errs = new ArrayList<Integer>();
            List<Integer> drops = new ArrayList<Integer>();
            ReceiverHelper.generateRandomErrDrop(errs, drops);
            fos = new FileOutputStream(sp.filePath + sp.fileName);
            boolean flag = true;
            int seq = 1;
            while (flag) {
                DataPacket dp = ReceiverHelper.receiveDatagramPacket(sock, bais, ois);
                AckPacket ack = new AckPacket((short) 8, dp.seqno);
                if (dp.isError()) {
                    PrintEachPacket.datagramReceivedPrint(PrintEachPacket.RECV, dp.seqno, PrintEachPacket.CRPT);
                } else if (dp.data.length == 0) {
                    flag = false;
                } else if (seq != dp.seqno) {
                    PrintEachPacket.datagramReceivedPrint(PrintEachPacket.DUPL, dp.seqno, PrintEachPacket.NOT_SEQ);
                    ReceiverHelper.sendAck(sock, bos, oos, ack);
                } else {
                    seq++;
                    ReceiverHelper.extractAndDeliver(fos, dp);
                    PrintEachPacket.datagramReceivedPrint(PrintEachPacket.RECV, dp.seqno, PrintEachPacket.RECV);
                    if (errs.contains(dp.seqno)) {
                        errs.remove(errs.indexOf(dp.seqno));
                        ack.cksum = 1;
                        ReceiverHelper.sendAck(sock, bos, oos, ack);
                        PrintEachPacket.ackSentPrint(ack.ackno, PrintEachPacket.CRPT);
                    } else if (drops.contains(dp.seqno)) {
                        drops.remove(drops.indexOf(dp.seqno));
                        PrintEachPacket.ackSentPrint(ack.ackno, PrintEachPacket.DROP);
                        ack.ackno = ack.ackno - 1;
                        ReceiverHelper.sendAck(sock, bos, oos, ack);
                        continue;
                    } else {
                        ReceiverHelper.sendAck(sock, bos, oos, ack);
                        PrintEachPacket.ackSentPrint(ack.ackno, PrintEachPacket.SENT);
                    }
                }
            }
            System.out.println("Received success!");
        } finally {
            ReceiverHelper.closeAll(sock, bos, oos, fos, bais, ois);
        }

    }
}
