package edu.metrostate;

/**
 * ICS460-01 Fall2021, Project 2, stop and wait, sender program - client side.
 * Instructor: Damodar Chetty Write by Team #3: Nalongsone Danddank, Asha Hassan
 */
// to helping print out each packet.
public class Print {
    // the keywords that use for print out each packet.
    public static final String SENDING = "SENDing";
    public static final String SENT = "SENT";
    public static final String DROP = "DROP";
    public static final String ERRR = "ERRR";
    public static final String ReSend = "ReSend";
    public static final String MoveWnd = "MoveWnd";
    public static final String ErrAck = "ErrAck";
    public static final String DuplAck = "DuplAck";

    // for printing the sending each datagram.
    public static void datagramSendPrint(String packetStatus, int seqNum, String status) {
        long time = System.currentTimeMillis();
        InputParameter sp = InputParameter.instance();
        String str = String.format("%s %3d %5d:%-5d %13d %s", packetStatus, seqNum, (seqNum - 1) * sp.packetSize,
                seqNum * sp.packetSize, time, status);
        System.out.println(str);
    }

    // for printing the ack receive of situation.
    public static void ackReceivedPrint(int seqNum, String status) {
        String str = String.format("AckRcvd %3d %s", seqNum, status);
        System.out.println(str);
    }

    // print out when time out.
    public static void timeOutPrint(int seqNum) {
        String str = String.format("TimeOut %3d", seqNum);
        System.out.println(str);
    }
}
