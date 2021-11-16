package edu.metrostate;

/**
 * ICS460-01 Fall2021, Project 2, stop and wait, sender program - client side.
 * Instructor: Damodar Chetty
 * Write by Team #3: 
 * 		 Nalongsone Danddank	
 */
import java.io.Serializable;

// AckPacket class to receive from receiver progream, to confirm or tell that data packet is success or not.
public class AckPacket implements Serializable {
    private static final long serialVersionUID = 1L;
    short cksum; // 16-bit 2-byte
    short len; // 16-bit 2-byte
    int ackno; // 32-bit 4-byte

    public AckPacket(short len, int ackno) {
        this.len = len;
        this.ackno = ackno;
        generateCksum();
    }

    // to generate check sum and set to cksum.
    protected void generateCksum() {
        short nob = (short) ((Math.floor(Math.log(len) / Math.log(2))) + 1);
        cksum += (short) (((1 << nob) - 1) ^ len);
        nob = (short) ((Math.floor(Math.log(ackno) / Math.log(2))) + 1);
        cksum += (short) (((1 << nob) - 1) ^ ackno);
    }

    // to tell the ack packet is error or not.
    public boolean isError() {
        if (cksum == 0)
            return false;
        if (cksum == 1)
            return true;
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

    @Override
    public String toString() {
        return "AckPacket [ackno=" + ackno + ", cksum=" + cksum + ", len=" + len + "]";
    }

}
