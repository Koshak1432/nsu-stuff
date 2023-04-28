package kosh.torrent.managers;

import kosh.torrent.structures.Peer;
import kosh.torrent.structures.Task;
import kosh.torrent.messages.IMessage;

/*
* Interface which describes methods of class working with file system
 */
public interface IDownloadUploadManager {
    void addTask(Task task);

    IMessage getOutgoingMsg(Peer peer);

    Integer getSuccessfulCheck();

    Integer getUnsuccessfulCheck();

}
