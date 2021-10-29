package edu.metrostate;

import java.util.Arrays;

public class DataPacket {
    private short cksum; // 16-bit 2-byte
    private short len; // 16-bit 2-byte
    private int ackno; // 32-bit 4-byte
    private int seqno; // 32-bit 4-byte
    private byte data[]; // 0-500 bytes.

    public DataPacket(short cksum, short len, int ackno, int seqno, byte[] data) {
        this.cksum = cksum;
        this.len = len;
        this.ackno = ackno;
        this.seqno = seqno;
        this.data = data;
    }

    public short getCksum() {
        return cksum;
    }

    public void setCksum(short cksum) {
        this.cksum = cksum;
    }

    public short getLen() {
        return len;
    }

    public void setLen(short len) {
        this.len = len;
    }

    public int getAckno() {
        return ackno;
    }

    public void setAckno(int ackno) {
        this.ackno = ackno;
    }

    public int getSeqno() {
        return seqno;
    }

    public void setSeqno(int seqno) {
        this.seqno = seqno;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DataPacket [ackno=" + ackno + ", len=" + len + ", cksum=" + cksum + ", seqno=" + seqno + ", data="
                + Arrays.toString(data) + "]";
    }

}
