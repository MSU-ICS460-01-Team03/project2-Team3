package edu.metrostate;

/**
 * ICS460-01 Fall2021, Project 2, stop and wait, Receiver program - server side.
 * Instructor: Damodar Chetty 
 * Write by Team #3: Nalongsone Danddank, Asha Hassan
 */
import java.util.Arrays;

// Data Packet class for collect the byte array and create the sequeue number to be a object packet.
public class DataPacket extends AckPacket {
	private static final long serialVersionUID = 1L;

	int seqno; // 32-bit 4-byte
	byte data[]; // 0-500 bytes.

	public DataPacket(short len, int ackno, int seqno, byte[] data) {
		super(len, ackno);
		this.seqno = seqno;
		this.data = data;
	}

	@Override
	public String toString() {
		return "DataPacket [ackno=" + ackno + ", len=" + len + ", cksum=" + cksum + ", seqno=" + seqno + ", data="
				+ Arrays.toString(data) + "]";
	}

}
