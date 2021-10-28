package sender;

public class SenderMain {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Notthing");
            return;
        }
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-t")) {
                System.out.println(args[i + 1]);
            } else if (args[i].equals("-d")) {
                System.out.println(args[i + 1]);
            }

        }

    }
}