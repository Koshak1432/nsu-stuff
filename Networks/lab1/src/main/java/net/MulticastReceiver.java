package net;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;

public class MulticastReceiver {
    public MulticastReceiver(String ip, String iface, int port) {
        try {
            datagramChannel = DatagramChannel.open();
            NetworkInterface networkInterface = NetworkInterface.getByName(iface);
            datagramChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            datagramChannel.bind(new InetSocketAddress(port)); //bind() controls which interface(s) the socket receives multicast packets from
            datagramChannel.setOption(StandardSocketOptions.IP_MULTICAST_IF, networkInterface);
            InetAddress inetAddress = InetAddress.getByName(ip);
            memberKey = datagramChannel.join(inetAddress, networkInterface);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SocketAddress receiveMsg() {
        ByteBuffer bb = ByteBuffer.allocate(BB_CAPACITY);
        SocketAddress addr;
        try {
            addr = datagramChannel.receive(bb);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        bb.flip();
        byte[] bytes = new byte[bb.limit()];
        bb.get(bytes, 0, bb.limit());
        return addr;
    }

    public DatagramChannel getDatagramChannel() {
        return datagramChannel;
    }

    private DatagramChannel datagramChannel;
    MembershipKey memberKey;

    private final int BB_CAPACITY = 1024;
}

