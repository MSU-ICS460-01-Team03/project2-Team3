package edu.metrostate;

/**
 * ICS460-01 Fall2021, Project 2, stop and wait, Receiver program - server side.
 * Instructor: Damodar Chetty Write by Team #3: Nalongsone Danddank, Asha Hassan
 */
// singleton class create for get the parameters from user input by command line
// and some other parameter that need to use on the entry program.
public class InputParameter {
    private static InputParameter singleton;

    // initilize default to all parameter if user input nothing.
    String filePath = System.getProperty("user.dir") + "/";
    String fileName = "cat.jpg";
    String receiverIpAddress = "localhost";
    String senderIpAddress = "localhost";
    int receiverPort = 58973;
    int senderPort = 58972;
    double percentError = 0.2;
    int packetSize = 500;
    int timeoutInterval = 2000;
    int totalPacket = 0;

    private InputParameter() {
    }

    // create only one instance object to use for entry program.
    public static InputParameter instance() {
        if (singleton == null)
            singleton = new InputParameter();
        return singleton;
    }

    // processing get argument from user input by command line. and if user typing
    // wrong than throw an InputException.
    public void getArgs(String[] args) throws InputException {
        if (args.length == 0)
            return;

        if (args[0].equals("-d")) {
            if (args[1].matches("\\d+\\.\\d+")) {
                percentError = Double.parseDouble(args[1]);
            } else {
                throw new InputException("Type wrong -d  percent drop: " + args[1]);
            }
        }
        if (args.length == 4) {
            if (args[3].matches("\\d+")) {
                receiverPort = Integer.parseInt(args[3]);
            } else {
                throw new InputException("Type port number wrong format: " + args[3]);
            }
            if (args[2].matches("\\d{1,3}:\\d{1,3}:\\d{1,3}:\\d{1,3}")) {
                receiverIpAddress = args[2];
            } else if (args[2].matches("localhost")) {
                receiverIpAddress = "localhost";
            } else {
                throw new InputException("Type receiver ip address wrong format: " + args[2]);
            }
            return;
        }
        throw new InputException("Type wrong command line!");
    }

    @Override
    public String toString() {
        return "ReceiverParameter [fileName=" + fileName + ", filePath=" + filePath + ", packetSize=" + packetSize
                + ", percentError=" + percentError + ", receiverIpAddress=" + receiverIpAddress + ", receiverPort="
                + receiverPort + ", senderIpAddress=" + senderIpAddress + ", senderPort=" + senderPort
                + ", timeoutInterval=" + timeoutInterval + ", totalPacket=" + totalPacket + "]";
    }

}
