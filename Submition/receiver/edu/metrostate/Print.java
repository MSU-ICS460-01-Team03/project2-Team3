package edu.metrostate;

/**
 * ICS460-01 Fall2021, Project 2, stop and wait, Receiver program - server side.
 * Instructor: Damodar Chetty Write by Team #3: Nalongsone Danddank, Asha Hassan
 */
// to helping print out each packet.
public class Print {
    // the keywords that use for print out each packet.
    public static final String RECV = "RECV";
    public static final String DUPL = "DUPL";
    public static final String NOT_SEQ = "!Seq";
    public static final String CRPT = "CRPT";
    public static final String ERR = "ERR";
    public static final String DROP = "DROP";
    public static final String SENT = "SENT";
    public static final String SENDing = "SENDing";

    // for printing the receiving each datagram.
    public static void datagramReceivedPrint(String packetStatus, int seqNum, String status) {
        long time = System.currentTimeMillis();
        String str = String.format("%s %13d %3d %s", packetStatus, time, seqNum, status);
        System.out.println(str);
    }

    // for printing the ack sending of situation.
    public static void ackSentPrint(int seqNum, String status) {
        long time = System.currentTimeMillis();
        String str = String.format("SENDing ACK %3d %13d %s", seqNum, time, status);
        System.out.println(str);
    }
}
