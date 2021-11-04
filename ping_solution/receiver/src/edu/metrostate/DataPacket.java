package edu.metrostate;

import java.util.Arrays;

public class DataPacket extends AckPacket {
    private static final long serialVersionUID = 1L;

    private int seqno; // 32-bit 4-byte
    private byte data[]; // 0-500 bytes.

    public DataPacket(short cksum, short len, int ackno, int seqno, byte[] data, String senderIP, String receiverIP) {
        super(len, ackno, senderIP, receiverIP);
        this.seqno = seqno;
        this.data = data;
    }

    public int getSeqno() {
        return seqno;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return "DataPacket [ackno=" + ackno + ", len=" + len + ", cksum=" + cksum + ", seqno=" + seqno + ", data="
                + Arrays.toString(data) + "]";
    }

}
