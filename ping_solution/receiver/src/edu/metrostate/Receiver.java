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
            System.out.println(errs);
            System.out.println(drops);
            fos = new FileOutputStream(sp.filePath + sp.fileName);
            boolean flag = true;
            int seq = 1;
            while (flag) {

                DataPacket dp = ReceiverHelper.receiveDatagramPacket(sock, bais, ois);
                if (dp.isError()) {
                    PrintEachPacket.datagramReceivedPrint(PrintEachPacket.RECV, dp.seqno, PrintEachPacket.CRPT);
                    // ReceiverHelper.sendAck(sock, bos, oos, dp.seqno, 0);
                } else if (dp.data.length == 0) {
                    flag = false;
                } else if (seq != dp.seqno) {
                    PrintEachPacket.datagramReceivedPrint(PrintEachPacket.DUPL, dp.seqno, PrintEachPacket.NOT_SEQ);
                    // ReceiverHelper.sendAck(sock, bos, oos, dp.seqno, 0);
                    // seq++;
                } else {
                    seq++;
                    ReceiverHelper.extractAndDeliver(fos, dp);
                    PrintEachPacket.datagramReceivedPrint(PrintEachPacket.RECV, dp.seqno, PrintEachPacket.RECV);
                    ReceiverHelper.sendAck(sock, bos, oos, dp.seqno, 0);
                }

            }

            System.out.println("Received success!");
        } finally {
            ReceiverHelper.closeAll(sock, bos, oos, fos, bais, ois);
        }

    }
}
