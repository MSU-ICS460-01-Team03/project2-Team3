package edu.metrostate;

import java.io.Serializable;

public class AckPacket implements Serializable {
    private static final long serialVersionUID = 1L;
    protected short cksum; // 16-bit 2-byte
    protected short len; // 16-bit 2-byte
    protected int ackno; // 32-bit 4-byte

    public AckPacket(short len, int ackno, String senderIP, String receiverIP) {
        this.len = len;
        this.ackno = ackno;
        generateCksum(senderIP, receiverIP);
    }

    protected void generateCksum(String senderIP, String receiverIP) {
        short nob = (short) ((Math.floor(Math.log(len) / Math.log(2))) + 1);
        cksum += (short) (((1 << nob) - 1) ^ len);
        nob = (short) ((Math.floor(Math.log(ackno) / Math.log(2))) + 1);
        cksum += (short) (((1 << nob) - 1) ^ ackno);
    }

    public boolean isError(String senderIP, String receiverIP) {
        short nob = (short) ((Math.floor(Math.log(len) / Math.log(2))) + 1);
        short sum = (short) (((1 << nob) - 1) ^ len);
        nob = (short) ((Math.floor(Math.log(ackno) / Math.log(2))) + 1);
        sum += (short) (((1 << nob) - 1) ^ ackno);
        nob = (short) ((Math.floor(Math.log(cksum) / Math.log(2))) + 1);
        sum += (short) (((1 << nob) - 1) ^ cksum);
        nob = (short) ((Math.floor(Math.log(sum) / Math.log(2))) + 1);
        sum = (short) (((1 << nob) - 1) ^ sum);
        // System.out.println(sum);
        return sum != 0;
    }

    public short getCksum() {
        return cksum;
    }

    public short getLen() {
        return len;
    }

    public int getAckno() {
        return ackno;
    }

    @Override
    public String toString() {
        return "AckPacket [ackno=" + ackno + ", cksum=" + cksum + ", len=" + len + "]";
    }

}
