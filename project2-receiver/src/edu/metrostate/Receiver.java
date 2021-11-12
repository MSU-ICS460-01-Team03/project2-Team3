/**
 * 
 */
package edu.metrostate;

/**
 * @author 
 *
 */

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
        InputParameter parameter = InputParameter.instance();
        parameter.getArgs(args);

        DatagramSocket sock = null;
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        FileOutputStream fos = null;

        try {
            sock = new DatagramSocket(parameter.receiverPort);
            System.out.println(">>>>>>>>>>>>>>>>>\nWaiting for receiving a file.....");
            receiveFirstData(sock, bais, ois);
            List<Integer> errs = new ArrayList<Integer>();
            List<Integer> drops = new ArrayList<Integer>();
            generateRandomErrDrop(errs, drops);
            fos = new FileOutputStream(parameter.filePath + parameter.fileName);
            boolean flag = true;
            int seq = 1;
            while (flag) {
                DataPacket dataPacket = receiveDatagramPacket(sock, bais, ois);
                AckPacket ack = new AckPacket((short) 8, dataPacket.seqno);
                if (dataPacket.isError()) {
                    datagramReceivedPrint(RECV, dataPacket.seqno, CRPT);
                } else if (dataPacket.data.length == 0) {
                    flag = false;
                } else if (seq != dataPacket.seqno) {
                    datagramReceivedPrint(DUPL, dataPacket.seqno, NOT_SEQ);
                    sendAck(sock, bos, oos, ack);
                } else {
                    seq++;
                    extractAndDeliver(fos, dataPacket);
                    datagramReceivedPrint(RECV, dataPacket.seqno, RECV);
                    if (errs.contains(dataPacket.seqno)) {
                        errs.remove(errs.indexOf(dataPacket.seqno));
                        ack.cksum = 1;
                        sendAck(sock, bos, oos, ack);
                        ackSentPrint(ack.ackno, CRPT);
                    } else if (drops.contains(dataPacket.seqno)) {
                        drops.remove(drops.indexOf(dataPacket.seqno));
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
