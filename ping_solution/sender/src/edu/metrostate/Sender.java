package edu.metrostate;

public class Sender {
    public static void main(String[] args) throws Exception {
        SenderParameter sp = new SenderParameter(args);
        System.out.println(sp);
        DataPacket dp = new DataPacket((short) 0, (short) 0, 0, 0, new byte[100]);
        System.out.println(dp);
        AckPacket ap = new AckPacket((short) 0, (short) 0, 1);
        System.out.println(ap);
    }
}
