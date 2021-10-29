package edu.metrostate;

public class Receiver {
    public static void main(String[] args) throws Exception {
        ReceiverParameter sp = new ReceiverParameter(args);
        System.out.println(sp);
        DataPacket dp = new DataPacket((short) 0, (short) 0, 0, 0, new byte[100]);
        System.out.println(dp);
        AckPacket ap = new AckPacket((short) 0, (short) 0, 1);
        System.out.println(ap);
    }
}
