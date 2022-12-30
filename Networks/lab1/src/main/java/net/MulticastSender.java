package net;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class MulticastSender {
    public MulticastSender(String iface) {
        try {
            datagramChannel = DatagramChannel.open();
            NetworkInterface ni = NetworkInterface.getByName(iface);
            datagramChannel.setOption(StandardSocketOptions.IP_MULTICAST_IF, ni);
            datagramChannel.bind(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String ip, int port, String msg) {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(ip, port);
        ByteBuffer bb = ByteBuffer.wrap(msg.getBytes());
        try {
            datagramChannel.send(bb, inetSocketAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DatagramChannel getDatagramChannel() {
        return datagramChannel;
    }

    private DatagramChannel datagramChannel;
}
