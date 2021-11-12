package edu.metrostate;

/**
 * @author 
 *
 */


public class Print {
    public static final String RECV = "RECV";
    public static final String DUPL = "DUPL";
    public static final String NOT_SEQ = "!Seq";
    public static final String CRPT = "CRPT";
    public static final String ERR = "ERR";
    public static final String DROP = "DROP";
    public static final String SENT = "SENT";
    public static final String SENDing = "SENDing";

    public static void datagramReceivedPrint(String packetStatus, int seqNum, String status) {
      
    }

    public static void ackSentPrint(int seqNum, String status) {
    
    }
}

