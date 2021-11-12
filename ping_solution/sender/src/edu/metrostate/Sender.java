package edu.metrostate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Sender {

    public static void main(String[] args) throws IOException, ParameterException, ClassNotFoundException,
            FileNotFoundException, InterruptedException {
        SenderParameter sp = SenderParameter.instance();
        sp.getArgs(args);
        DatagramSocket sock = null;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        List<DataPacket> sendPacks = null;
        try {
            sock = new DatagramSocket(sp.senderPort);
            sock.setSoTimeout(sp.timeoutInterval);
            sendPacks = SenderHelper.makePacketList();
            SenderHelper.sendHeadPacket(sendPacks.size(), sock, bos, oos);

            List<Integer> errs = new ArrayList<Integer>();
            List<Integer> drops = new ArrayList<Integer>();
            SenderHelper.generateRandomErrDropTimeOut(errs, drops);

            Iterator<DataPacket> iter = sendPacks.iterator();
            DataPacket dp = iter.next();
            SenderHelper.sendDatagramPacket(sock, dp, bos, oos);
            PrintEachPacket.datagramSendPrint(PrintEachPacket.SENDING, dp.seqno, PrintEachPacket.SENT);
            while (iter.hasNext()) {
                AckPacket ack = SenderHelper.receiveAck(sock, bais, ois);
                if (ack != null && !ack.isError() && ack.ackno == dp.seqno) {
                    PrintEachPacket.ackReceivedPrint(ack.ackno, PrintEachPacket.MoveWnd);
                    if (drops.contains(dp.seqno)) {
                        drops.remove(drops.indexOf(dp.seqno));
                        PrintEachPacket.datagramSendPrint(PrintEachPacket.SENDING, dp.seqno, PrintEachPacket.DROP);
                        continue;
                    }
                    dp = iter.next();
                    if (errs.contains(dp.seqno)) {
                        errs.remove(errs.indexOf(dp.seqno));
                        dp.cksum = 1;
                        PrintEachPacket.datagramSendPrint(PrintEachPacket.SENDING, dp.seqno, PrintEachPacket.ERRR);
                    } else {
                        PrintEachPacket.datagramSendPrint(PrintEachPacket.SENDING, dp.seqno, PrintEachPacket.SENT);
                    }
                    SenderHelper.sendDatagramPacket(sock, dp, bos, oos);
                } else if (ack != null && ack.ackno != dp.seqno) {
                    PrintEachPacket.ackReceivedPrint(ack.ackno, PrintEachPacket.DuplAck);
                } else if (ack != null && ack.isError()) {
                    PrintEachPacket.ackReceivedPrint(ack.ackno, PrintEachPacket.ErrAck);
                    continue;
                } else {
                    if (ack == null) {
                        System.out.println("TimeOut " + dp.seqno);
                    }
                    PrintEachPacket.datagramSendPrint(PrintEachPacket.ReSend, dp.seqno, PrintEachPacket.SENT);
                    dp.cksum = 0;
                    SenderHelper.sendDatagramPacket(sock, dp, bos, oos);
                }
            }
            dp = new DataPacket((short) 0, 0, 0, new byte[0]);
            SenderHelper.sendDatagramPacket(sock, dp, bos, oos);
            System.out.println("Sent! success.");
        } finally {
            SenderHelper.closeAll(sock, bos, oos, bais);

        }

    }
}
