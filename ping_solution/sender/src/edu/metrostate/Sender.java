package edu.metrostate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.SocketException;
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
            sendPacks = SenderHelper.makePacketList(sp);
            SenderHelper.sendHeadPacket(sp, sendPacks.size(), sock, bos, oos);
            List<Integer> errs = new ArrayList<Integer>();
            List<Integer> drops = new ArrayList<Integer>();
            List<Integer> timeOuts = new ArrayList<Integer>();
            SenderHelper.generateRandomErrDropTimeOut(sp, errs, drops, timeOuts);
            System.out.println(errs);
            System.out.println(drops);
            System.out.println(timeOuts);
            Iterator<DataPacket> iter = sendPacks.iterator();
            DataPacket dp = iter.next();
            SenderHelper.sendDatagramPacket(sock, dp, sp, bos, oos);
            while (iter.hasNext()) {
                AckPacket ack = SenderHelper.receiveAck(sock, sp, bais, ois);
                System.out.println(ack.ackno);
                System.out.println(dp.seqno);
                if (ack != null && !ack.isError() && ack.ackno == dp.seqno) {
                    dp = iter.next();
                    if (errs.contains(dp.seqno)) {
                        dp.cksum = 1;
                    }
                    PrintEachPacket.datagramSendPrint(PrintEachPacket.SENDING, dp.seqno, dp.seqno,
                            sp.packetSize * dp.seqno, PrintEachPacket.SENT);
                    SenderHelper.sendDatagramPacket(sock, dp, sp, bos, oos);
                } else { // } if (ack.ackno != dp.seqno) {
                    PrintEachPacket.datagramSendPrint(PrintEachPacket.ReSend, dp.seqno, dp.seqno,
                            sp.packetSize * dp.seqno, PrintEachPacket.SENT);
                    SenderHelper.sendDatagramPacket(sock, dp, sp, bos, oos);
                }

                Thread.sleep(25);
            }
            dp = new DataPacket((short) 0, 0, 0, new byte[0]);
            SenderHelper.sendDatagramPacket(sock, dp, sp, bos, oos);
            System.out.println("Sent! success.");

        } finally {
            SenderHelper.closeAll(sock, bos, oos, bais);

        }

    }
}
