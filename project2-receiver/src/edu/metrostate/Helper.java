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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.Random;

// for helping Receiver class and extend writing function that use for complete the Receiver program.
public class Helper extends Print {
	// create the process to receive the data like filename and total number packet
	// that will be received.
	@SuppressWarnings("unchecked")
	public static void receiveFirstData(DatagramSocket sock, ByteArrayInputStream bais, ObjectInputStream ois)
			throws IOException, ClassNotFoundException {
		InputParameter parameter = InputParameter.instance();
		byte[] data = new byte[1024];
		DatagramPacket inPack = new DatagramPacket(data, data.length);
		sock.receive(inPack);
		byte[] recData = inPack.getData();
		bais = new ByteArrayInputStream(recData);
		ois = new ObjectInputStream(bais);
		Map<String, Integer> headPack = (Map<String, Integer>) ois.readObject();
		for (Map.Entry<String, Integer> e : headPack.entrySet()) {
			parameter.fileName = e.getKey();
			parameter.totalPacket = e.getValue();
		}
		System.out.println("Start receive file name: " + parameter.fileName + ", with total: " + parameter.totalPacket
				+ " packets");
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

	// processing receive datagram packet and read object input stream.
	public static DataPacket receiveDatagramPacket(DatagramSocket sock, ByteArrayInputStream bais,
			ObjectInputStream ois) throws IOException, ClassNotFoundException {
		byte[] data = new byte[1024];
		DatagramPacket inPack = new DatagramPacket(data, data.length);
		sock.receive(inPack);
		byte[] recData = inPack.getData();
		bais = new ByteArrayInputStream(recData);
		ois = new ObjectInputStream(bais);
		DataPacket dataPacket = (DataPacket) ois.readObject();
		return dataPacket;
	}

	// extract data packet and delever data to file.
	public static void extractAndDeliver(FileOutputStream fos, DataPacket dataPacket) throws IOException {
		fos.write(dataPacket.data, 0, dataPacket.len - 12);
		fos.flush();
	}

	// processing send ack object back to sender program.
	public static void sendAck(DatagramSocket sock, ByteArrayOutputStream bos, ObjectOutputStream oos, AckPacket ack)
			throws IOException {
		InputParameter parameter = InputParameter.instance();
		bos = new ByteArrayOutputStream();
		oos = new ObjectOutputStream(bos);
		oos.writeObject(ack);
		oos.flush();
		byte[] sendData = bos.toByteArray();
		InetAddress address = InetAddress.getByName(parameter.senderIpAddress);
		DatagramPacket sendPack = new DatagramPacket(sendData, sendData.length, address, parameter.senderPort);
		sock.send(sendPack);
	}

	// when everything done or terminate, close all stream finally.
	public static void closeAll(DatagramSocket sock, ByteArrayOutputStream bos, ObjectOutputStream oos,
			FileOutputStream fos, ByteArrayInputStream bais, ObjectInputStream ois) throws IOException {
		if (fos != null)
			fos.close();
		if (bais != null)
			bais.close();
		if (ois != null)
			ois.close();
		if (bos != null)
			bos.close();
		if (oos != null)
			oos.close();
		if (sock != null) {
			sock.close();
		} else {
			sock = null;
		}
	}
}
