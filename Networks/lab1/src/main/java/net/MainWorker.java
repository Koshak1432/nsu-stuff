package net;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.*;

public class MainWorker extends Thread {
    MainWorker(String multicastIp, int port, String iface) {
        this.multicastIp = multicastIp;
        this.multicastPort = port;
        this.multicastIface = iface;
    }

    @Override
    public void run() {
        Selector selector;
        try {
            selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        MulticastReceiver receiver = new MulticastReceiver(multicastIp, multicastIface, multicastPort); // получает сообщения от мультикаст группы
        MulticastSender sender = new MulticastSender(multicastIface); // отправляет сообщения на мультикаст группу
        DatagramChannel recChannel = receiver.getDatagramChannel();
        DatagramChannel sendChannel = sender.getDatagramChannel();
        try {
            recChannel.configureBlocking(false);
            sendChannel.configureBlocking(false);
            recChannel.register(selector, SelectionKey.OP_READ);
            sendChannel.register(selector, SelectionKey.OP_WRITE);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        boolean hasChanges = false;
        String msg = "keep alive";
        long currentTime;
        long lastTimeSent = 0;

        while(true) {
            currentTime = System.currentTimeMillis();
            try {
                int ready = selector.select();
                if (ready == 0) {
                    continue;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            Set<SelectionKey> keys =selector.selectedKeys();
            for (SelectionKey key : keys) {
                if (!key.isValid()) {
                    continue;
                }
                if (key.isReadable()) {
                    SocketAddress addr = receiver.receiveMsg();
                    if (!senders.containsKey(addr)) {
                        hasChanges = true;
                    }
                    senders.put(addr, currentTime);
                }
                if (key.isWritable() && (currentTime - lastTimeSent) > timeoutToSend) {
                    sender.sendMsg(multicastIp, multicastPort, msg);
                    lastTimeSent = currentTime;
                }
            }
            selector.selectedKeys().clear();
            hasChanges |= deleteInactiveConnections(currentTime);
            if (hasChanges) {
                printConnections();
                hasChanges = false;
            }
        }
    }

    private boolean deleteInactiveConnections(long currentTime) {
        boolean hasInactive;
        List<SocketAddress> toDelete = new ArrayList<>();
        findInactiveConnections(currentTime, toDelete);
        hasInactive = toDelete.size() > 0;
        for (var addr : toDelete) {
            senders.remove(addr);
        }
        return hasInactive;
    }

    private void findInactiveConnections(long currentTime, List<SocketAddress> toDelete) {
        for (var entry : senders.entrySet()) {
            if (currentTime - entry.getValue() > timeoutToDrop) {
                toDelete.add(entry.getKey());
            }
        }
    }

    private void printConnections() {
        System.out.println("-------------------------");
        System.out.println("Alive connections:");
        for (var connection : senders.keySet()) {
            System.out.println(connection);
        }
        System.out.println("-------------------------");
    }


    private final String multicastIp;
    private final int multicastPort;
    private final String multicastIface;
    private final Map<SocketAddress, Long> senders = new HashMap<>();
    private final long timeoutToDrop = 1500;
    private final long timeoutToSend = 500;
}
