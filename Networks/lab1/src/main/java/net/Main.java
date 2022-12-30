package net;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Gimme multicast address");
            return;
        }
        String[] address = args[0].split("/");
        String MULTICAST_IP = address[0];
        int MULTICAST_PORT = Integer.parseInt(address[1]);
        System.out.println("IP: " + MULTICAST_IP + ", PORT: " + MULTICAST_PORT);

//        String MULTICAST_IP = "230.0.0.1";
//        String MULTICAST_IP = "FF7E:230::1234";
//        int MULTICAST_PORT = 4444;
//        Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
//        while (ifaces.hasMoreElements()) {
//            NetworkInterface ni = ifaces.nextElement();
//            if (ni.isLoopback() || !ni.isUp()) {
//                continue;
//            }
//            System.out.println(ni);
//        }
        String MULTICAST_NI = "eth4";
        MainWorker worker = new MainWorker(MULTICAST_IP, MULTICAST_PORT, MULTICAST_NI);
        worker.start();
        try {
            worker.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}