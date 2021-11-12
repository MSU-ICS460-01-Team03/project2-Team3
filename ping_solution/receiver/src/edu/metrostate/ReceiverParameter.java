package edu.metrostate;

public class ReceiverParameter {
    private static ReceiverParameter singleton;

    String filePath = System.getProperty("user.dir") + "/";
    String fileName = "panda.jpg";
    String receiverIpAddress = "localhost";
    String senderIpAddress = "localhost";
    int receiverPort = 58973;
    int senderPort = 58972;
    double percentError = 0.2;
    int packetSize = 500;
    int timeoutInterval = 2000;
    int totalPacket = 0;

    private ReceiverParameter() {
    }

    public static ReceiverParameter instance() {
        if (singleton == null)
            singleton = new ReceiverParameter();
        return singleton;
    }

    public void getArgs(String[] args) throws ParameterException {
        if (args.length == 0) {
            return;
        }

        if (args[0].equals("-d")) {
            if (args[1].matches("\\d+\\.\\d+")) {
                percentError = Double.parseDouble(args[1]);
            } else {

                throw new ParameterException("Type wrong -d  percent drop: " + args[1]);
            }
        }
        if (args.length == 4) {
            if (args[3].matches("\\d+")) {
                receiverPort = Integer.parseInt(args[3]);
            } else {
                throw new ParameterException("Type port number wrong format: " + args[3]);
            }
            if (args[2].matches("\\d{1,3}:\\d{1,3}:\\d{1,3}:\\d{1,3}")) {
                receiverIpAddress = args[2];
            } else if (args[2].matches("localhost")) {
                receiverIpAddress = "localhost";
            } else {
                throw new ParameterException("Type receiver ip address wrong format: " + args[2]);
            }
            return;
        }
        throw new ParameterException("Type wrong command line!");

    }

    @Override
    public String toString() {
        return "ReceiverParameter [fileName=" + fileName + ", filePath=" + filePath + ", packetSize=" + packetSize
                + ", percentError=" + percentError + ", receiverIpAddress=" + receiverIpAddress + ", receiverPort="
                + receiverPort + ", senderIpAddress=" + senderIpAddress + ", senderPort=" + senderPort
                + ", timeoutInterval=" + timeoutInterval + ", totalPacket=" + totalPacket + "]";
    }

}
