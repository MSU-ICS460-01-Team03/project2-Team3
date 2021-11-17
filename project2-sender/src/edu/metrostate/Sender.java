package edu.metrostate;

/**
 * ICS460-01 Fall2021, Project 2, stop and wait, sender program - client side.
 * Instructor: Damodar Chetty
 * Write by Team #3: 
 * 		 Nalongsone Danddank, Asha Hassan	
 */
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Sender extends Helper {
	public static void main(String[] args)
			throws IOException, InputException, ClassNotFoundException, FileNotFoundException, InterruptedException {
		// Initilize singleton instance object for get parameter from input
		// command line and use it in anywhere when program running.
		InputParameter parameter = InputParameter.instance();
		parameter.getArgs(args);
		/////////////
		// Initilize and prepare for processing each sending packet
		DatagramSocket sock = null;
		ByteArrayOutputStream bos = null;
		ObjectOutputStream oos = null;
		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;
		List<DataPacket> sendPacks = null;
		////////////////
		try {
			// Create Socket UDP by a port
			sock = new DatagramSocket(parameter.senderPort);

			// set time out interval for waiting.
			// then, if time run out without receive ack back,
			// socket will resend packet again.
			sock.setSoTimeout(parameter.timeoutInterval);

			// making packet to list from the file which user input.
			sendPacks = makePacketList();

			// sending the head packet to tell receiver about what filename and total number
			// of packet will be sent.
			sendHeadPacket(sendPacks.size(), sock, bos, oos);

			// Follow by user input percentage (-d),
			// build the random number list of data packet index for error and drop,
			// for simulating situation sending packet error, drop, time out,
			List<Integer> errs = new ArrayList<Integer>();
			List<Integer> drops = new ArrayList<Integer>();
			generateRandomErrDrop(errs, drops);
			///////////////////////////////////

			// sending first packet for waiting for the first ack.
			Iterator<DataPacket> iter = sendPacks.iterator();
			DataPacket dataPacket = iter.next();
			sendDatagramPacket(sock, dataPacket, bos, oos);
			datagramSendPrint(SENDING, dataPacket.seqno, SENT);
			////////////////////

			// Looping util every sending packet done.
			while (iter.hasNext()) {
				// waiting for receive ack packet.
				// if ack get null that mean time out, and resend data packet again.
				AckPacket ack = receiveAck(sock, bais, ois);
				if (ack != null && !ack.isError() && ack.ackno == dataPacket.seqno) {
					// when received ack without any problem, then move to next window
					ackReceivedPrint(ack.ackno, MoveWnd);
					dataPacket = iter.next();
					///////////
					// As having simulated some data packet must drop, error, and time ou,
					// so if any data packet's seqno match to a number in the those list,
					// the packet must follow by simulating, then waiting for resend data packet
					// again.
					if (drops.contains(dataPacket.seqno)) {
						drops.remove(drops.indexOf(dataPacket.seqno));
						datagramSendPrint(SENDING, dataPacket.seqno, DROP);
						// when packet drop just go to next loop.
						continue;
					} else if (errs.contains(dataPacket.seqno)) {
						errs.remove(errs.indexOf(dataPacket.seqno));
						// set cksum to bad before sending.
						dataPacket.cksum = 1;
						datagramSendPrint(SENDING, dataPacket.seqno, ERRR);
					} else {
						datagramSendPrint(SENDING, dataPacket.seqno, SENT);
					}
					sendDatagramPacket(sock, dataPacket, bos, oos);
				} else if (ack != null && ack.ackno != dataPacket.seqno) {
					// when get duplicate ack packet print it out .
					ackReceivedPrint(ack.ackno, DuplAck);
				} else if (ack != null && ack.isError()) {
					// when get a error ack packet print it out.
					ackReceivedPrint(ack.ackno, ErrAck);
					continue;
				} else {
					if (ack == null) {
						// print time out occurred for the dataPacket
						timeOutPrint(dataPacket.seqno);
					}
					// Resend the last data packeet again.
					datagramSendPrint(ReSend, dataPacket.seqno, SENT);
					dataPacket.cksum = 0;
					sendDatagramPacket(sock, dataPacket, bos, oos);
					/////////////
				}
			}
			// when finish sending all packet without no problem,
			// sending the last data packet for telling receiver server terminate.
			dataPacket = new DataPacket((short) 0, 0, 0, new byte[0]);
			sendDatagramPacket(sock, dataPacket, bos, oos);
			/////////////////
			System.out.println("Sent! success.");
		} finally {
			closeAll(sock, bos, oos, bais);
			System.out.println("Sender Program Terminated!");
		}

	}
}
