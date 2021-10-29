package edu.metrostate;

public class SenderParameter {
    String senderIpAddress = "localhost";
    int senderPort = 58972;
    String filePath = System.getProperty("user.dir") + "/";
    String fileName = "panda.jpg";
    int packetSize = 100;
    int timeoutInterval = 3000;
    String receiverIpAddress = "localhost";
    int receiverPort = 58973;
    double percentError = 0.25;

    public SenderParameter(String[] args) throws ParameterException {
        getArgs(args);
    }

    private void getArgs(String[] args) throws ParameterException {
        if (args.length == 0) {
            return;
        }
        if (args.length == 1) {
            if (args[0].matches(".+\\.\\w+")) {
                fileName = args[0];
                return;
            }
            throw new ParameterException("Type file name wrong format.");
        }
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-s")) {
                if (args[i + 1].matches("\\d+")) {
                    packetSize = Integer.parseInt(args[i + 1]);
                } else {
                    throw new ParameterException("Type wrong -s size packet number format: " + args[i + 1]);
                }
            } else if (args[i].equals("-t")) {
                if (args[i + 1].matches("\\d+")) {
                    timeoutInterval = Integer.parseInt(args[i + 1]);
                } else {
                    throw new ParameterException("Type wrong -t  timeout interval: " + args[i + 1]);
                }
            } else if (args[i].equals("-d")) {
                if (args[i + 1].matches("\\d+\\.\\d+")) {
                    percentError = Double.parseDouble(args[i + 1]);
                } else {
                    throw new ParameterException("Type wrong -d  percent drop: " + args[i + 1]);
                }
            } else if (args[i].equals("-f")) {
                if (args[i + 1].matches("\\w+\\.\\w+")) {
                    fileName = args[i + 1];
                } else {
                    throw new ParameterException("Type file name wrong format: " + args[i + 1]);
                }
            }

        }
        if (args[args.length - 1].matches("\\d+")) {
            receiverPort = Integer.parseInt(args[args.length - 1]);
        } else {
            throw new ParameterException("Type port number wrong format: " + args[args.length - 1]);
        }
        if (args[args.length - 2].matches("\\d{1,3}:\\d{1,3}:\\d{1,3}:\\d{1,3}")) {
            receiverIpAddress = args[args.length - 2];
        } else if (args[args.length - 2].matches("localhost")) {
            receiverIpAddress = "localhost";
        } else {
            throw new ParameterException("Type receiver ip address wrong format: " + args[args.length - 2]);
        }

    }

    @Override
    public String toString() {
        return "SenderParameter [fileName=" + fileName + ", filePath=" + filePath + ", packetSize=" + packetSize
                + ", percentError=" + percentError + ", receiverIpAddress=" + receiverIpAddress + ", receiverPort="
                + receiverPort + ", senderIpAddress=" + senderIpAddress + ", senderPort=" + senderPort
                + ", timeoutInterval=" + timeoutInterval + "]";
    }
}
