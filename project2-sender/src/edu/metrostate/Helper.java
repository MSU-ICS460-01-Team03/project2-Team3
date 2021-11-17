package edu.metrostate;

/**
 * ICS460-01 Fall2021, Project 2, stop and wait, sender program - client side.
 * Instructor: Damodar Chetty
 * Write by Team #3: 
 * 		 Nalongsone Danddank, Asha Hassan	
 */
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

// for helping Sender class and extend writing function that use for complete the Sender program.
public class Helper extends Print {
	// build the DataPacket List that come from a moung of bytes array of file that
	// want to send.
	public static List<DataPacket> makePacketList() throws IOException {
		// get the singleton instance of input parameter object.
		InputParameter parameter = InputParameter.instance();
		String fileStr = parameter.filePath + parameter.fileName;
		File myFile = null;
		FileInputStream fis = null;
		try {
			myFile = new File(fileStr);
			fis = new FileInputStream(myFile);
			List<DataPacket> sendPacks = new ArrayList<>();
			// create data array bytes by the size that get from user input by command line.
			byte[] data = new byte[parameter.packetSize];
			int i = 1;
			int count = 0;
			while ((count = fis.read(data)) != -1) {
				sendPacks.add(new DataPacket((short) (count + 12), i, i, data.clone()));
				i++;
			}
			// save total number packet that we just build to parameter for use in the next.
			parameter.totalPacket = sendPacks.size();
			return sendPacks;
		} finally {
			if (fis != null)
				fis.close();
		}
	}

	// generate random numbers of error and drop to array list.
	public static void generateRandomErrDrop(List<Integer> errs, List<Integer> drops) {
		InputParameter parameter = InputParameter.instance();
		Random random = new Random();
		int errDrop = (int) ((parameter.totalPacket / 2) * parameter.percentError);
		for (int i = 0; i < errDrop; i++) {
			errs.add(random.nextInt(parameter.totalPacket - 2) + 2);
			drops.add(random.nextInt(parameter.totalPacket - 2) + 2);
		}
	}

	// processing sending the first packet to tell reveiver program about filename
	// and total number packet.
	public static void sendHeadPacket(int sendPacksSize, DatagramSocket sock, ByteArrayOutputStream bos,
			ObjectOutputStream oos) throws IOException {
		InputParameter parameter = InputParameter.instance();
		Map<String, Integer> headPacket = new HashMap<>();
		headPacket.put(parameter.fileName, sendPacksSize);
		bos = new ByteArrayOutputStream();
		oos = new ObjectOutputStream(bos);
		oos.writeObject(headPacket);
		oos.flush();
		byte[] sendData = bos.toByteArray();
		DatagramPacket sendPack = new DatagramPacket(sendData, sendData.length,
				InetAddress.getByName(parameter.receiverIpAddress), parameter.receiverPort);
		sock.send(sendPack);
		System.out.println(
				"Start Sending file name: " + parameter.fileName + ", with total: " + sendPacksSize + " packets");
	}

	// processing write the object packet and sending datagram packet to receicer.
	public static void sendDatagramPacket(DatagramSocket sock, DataPacket dp, ByteArrayOutputStream bos,
			ObjectOutputStream oos) throws IOException {
		InputParameter parameter = InputParameter.instance();
		bos = new ByteArrayOutputStream();
		oos = new ObjectOutputStream(bos);
		oos.writeObject(dp);
		oos.flush();
		byte[] sendData = bos.toByteArray();
		DatagramPacket sendPack = new DatagramPacket(sendData, sendData.length,
				InetAddress.getByName(parameter.receiverIpAddress), parameter.receiverPort);
		sock.send(sendPack);

	}

	// processing receive ack object packet.
	public static AckPacket receiveAck(DatagramSocket sock, ByteArrayInputStream bais, ObjectInputStream ois)
			throws IOException, ClassNotFoundException {
		InputParameter parameter = InputParameter.instance();
		byte[] data = new byte[parameter.packetSize];
		DatagramPacket inPack = new DatagramPacket(data, data.length);
		try {
			// waiting for amoung of time that set from beginning.
			sock.receive(inPack);
		} catch (SocketTimeoutException ste) {
			// when time out and get the exception just return null to resend data packet
			// object again.
			return null;
		}
		// when get ack on time, just go ahead.
		byte[] recData = inPack.getData();
		bais = new ByteArrayInputStream(recData);
		ois = new ObjectInputStream(bais);
		AckPacket ack = (AckPacket) ois.readObject();
		return ack;
	}

	// when everything done or terminate, close all stream finally.
	public static void closeAll(DatagramSocket sock, ByteArrayOutputStream bos, ObjectOutputStream oos,
			ByteArrayInputStream bais) throws IOException {
		if (sock != null)
			sock.close();
		if (bos != null)
			bos.close();
		if (oos != null)
			oos.close();
		if (bais != null)
			bais.close();
	}
}
