package kosh.torrent.messages;

import kosh.torrent.structures.Peer;

/*
* Interface which describes BitTorrent protocol messages sender
 */
public interface IMessagesSender {
    boolean sendMsg(Peer peer, IMessage msg);
}
