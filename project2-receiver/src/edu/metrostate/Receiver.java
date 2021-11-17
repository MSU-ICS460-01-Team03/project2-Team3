package edu.metrostate;

/**
 * ICS460-01 Fall2021, Project 2, stop and wait, Receiver program - server side.
 * Instructor: Damodar Chetty
 * Write by Team #3: 
 * 		 Nalongsone Danddank, Asha Hassan	
 */
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

public class Receiver extends Helper {
	public static void main(String[] args)
			throws IOException, InputException, ClassNotFoundException, InterruptedException {
		// Initilize singleton instance object for get parameter from input
		// command line and use it in anywhere when program running.
		InputParameter parameter = InputParameter.instance();
		parameter.getArgs(args);
		/////////////
		// Initilize and prepare for processing each received packet
		DatagramSocket sock = null;
		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;
		ByteArrayOutputStream bos = null;
		ObjectOutputStream oos = null;
		FileOutputStream fos = null;
		///////////////
		try {
			// Create Socket UDP by a port
			sock = new DatagramSocket(parameter.receiverPort);
			System.out.println(">>>>>>>>>>>>>>>>>\nWaiting for receiving a file.....");

			// receive first data that sender tell receiver like filename and total number
			// of packer will be received.
			receiveFirstData(sock, bais, ois);

			// Follow by user input percentage (-d),
			// build the random number list of ack packet index for error and drop,
			// for simulating situation sending ack packet error, drop.
			List<Integer> errs = new ArrayList<Integer>();
			List<Integer> drops = new ArrayList<Integer>();
			generateRandomErrDrop(errs, drops);
			/////////////////////////////////

			// setup a file with the name that receive from the first data.
			fos = new FileOutputStream(parameter.filePath + parameter.fileName);
			// set flag to loop until received all packet, then when receive the last
			// packet, set flag to false to signal to terminate the program.
			boolean flag = true;
			// set the first object packet of sequeue to 1 and increaseing to the next
			// packet if success received.
			int seq = 1;
			while (flag) {
				// waiting for receive data packet.
				DataPacket dataPacket = receiveDatagramPacket(sock, bais, ois);
				// create a ack instance for send back to tell sender program.
				AckPacket ack = new AckPacket((short) 8, dataPacket.seqno);
				// check data packet if error
				if (dataPacket.isError()) {
					// than print out it corrupt and waiting for the correct one.
					datagramReceivedPrint(RECV, dataPacket.seqno, CRPT);
				} else if (dataPacket.data.length == 0) {
					// if receive the data packet that data.length is 0, that mean the sender alread
					// sent all packet and closed. so set flag to false to terminate the program.
					flag = false;
				} else if (seq != dataPacket.seqno) {
					// if the sequeue number of data packet that just receive is not match to the
					// sequeue number which expectation. than print out, duplicate and wrong
					// sequeue number.
					datagramReceivedPrint(DUPL, dataPacket.seqno, NOT_SEQ);
					// than send ack back to tell sender.
					sendAck(sock, bos, oos, ack);
				} else {
					// when every thing ok with no problem, increasing the sequeue for waiting
					// receive the next data packet.
					seq++;
					// extract data packet object and deliver the receive file.
					extractAndDeliver(fos, dataPacket);
					// print out show that receive data packet successfully.
					datagramReceivedPrint(RECV, dataPacket.seqno, RECV);
					/////////////////////////////
					// As having simulated situation that some ack packet must drop, error,
					// so if any ack packet's seqno match to a number in the those list,
					// the packet must follow by simulating, then resend ack packet again.
					if (errs.contains(dataPacket.seqno)) {
						errs.remove(errs.indexOf(dataPacket.seqno));
						// set ack check sum to bad, 1. that mean sending error ack object to sender.
						ack.cksum = 1;
						sendAck(sock, bos, oos, ack);
						// then, print out, ack corrupted sent.
						ackSentPrint(ack.ackno, CRPT);
					} else if (drops.contains(dataPacket.seqno)) {
						drops.remove(drops.indexOf(dataPacket.seqno));
						// print out, ack droped.
						ackSentPrint(ack.ackno, DROP);
						// assuming that we sent the last ack back.
						ack.ackno = ack.ackno - 1;
						sendAck(sock, bos, oos, ack);
						// when ack packet set to be drop just continue to next loop.
						continue;
					} else {
						// when every thing ok, send the correct and good ack object packet back to
						// sender, then print it out.
						sendAck(sock, bos, oos, ack);
						ackSentPrint(ack.ackno, SENT);
					}
				}
			}
			System.out.println("Received success!");
		} finally {
			closeAll(sock, bos, oos, fos, bais, ois);
			System.out.println("Receiver Program Terminated!");
		}

	}
}
