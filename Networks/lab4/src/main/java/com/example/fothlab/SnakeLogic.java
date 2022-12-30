package com.example.fothlab;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.example.fothlab.SnakeProto.*;

public class SnakeLogic {
    public SnakeLogic(NotifiableClient clientUI) throws IOException {
        unacknowledgedMessages = new HashMap<>();
        unacknowledgedMessagesToMaster = new HashMap<>();
        lastReceivingMomentsForPlayers = new HashMap<>();
        lastSendingMomentsForPlayers = new HashMap<>();
        addrToIdMap = new HashMap<>();
        newlyDiedToInform = new ArrayList<>();
        directionCommands = new ConcurrentLinkedQueue<>();
        inviteQueue = new ConcurrentLinkedQueue<>();
        msgSeq = 1;
        id = 0;
        role = NodeRole.NORMAL;
        currentStateBuilder = null;
        selector = null;
        this.clientUI = clientUI;
        multicastReceivingChannel = DatagramChannel.open(StandardProtocolFamily.INET)
                .setOption(StandardSocketOptions.SO_REUSEADDR, true)
                .bind(new InetSocketAddress(MULTICAST_PORT));
        multicastReceivingChannel.configureBlocking(false);
        InetAddress multicastGroupAddress = InetAddress.getByName(MULTICAST_GROUP_ADDRESS);
        multicastGroupSocketAddress = new InetSocketAddress(multicastGroupAddress, MULTICAST_PORT);
        multicastReceivingChannel.join(multicastGroupAddress,
                                        NetworkInterface.getByName("wlan0"));
        mainChannel = DatagramChannel.open(StandardProtocolFamily.INET)
                .bind(null);
        mainChannel.configureBlocking(false);
    }

    public void searchForAvailableGames() {
        try {
            GameMessage discoverMsg = GameMessage.newBuilder()
                    .setMsgSeq(msgSeq)
                    .setDiscover(GameMessage.DiscoverMsg.newBuilder().build())
                    .build();
            sendMsg(mainChannel, discoverMsg, multicastGroupSocketAddress);
            SocketAddress serverAddress = new InetSocketAddress("snakes.ippolitov.me", 9192);
            sendMsg(mainChannel, discoverMsg, serverAddress);
            ByteBuffer msgReceivingBuffer = ByteBuffer.allocate(MAX_MESSAGE_LENGTH_BYTES);
            List<GameAndMasterAddress> games = new ArrayList<>();
            try (Selector selector = Selector.open()) {
                this.selector = selector;
                multicastReceivingChannel.register(selector, SelectionKey.OP_READ);
                boolean listModified = true;
                while (!Thread.currentThread().isInterrupted()) {
                    if (listModified) {
                        clientUI.notifyOnGamesListChange(games);
                        listModified = false;
                    }
                    selector.select();
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    for (SelectionKey key : selectedKeys) {
                        if ((key.isReadable()) && (Objects.equals(key.channel(), multicastReceivingChannel))) {
                            SocketAddress source = multicastReceivingChannel.receive(msgReceivingBuffer);
                            msgReceivingBuffer.flip();
                            GameMessage msgReceived = GameMessage.parseFrom(msgReceivingBuffer);
                            msgReceivingBuffer.clear();
                            if (msgReceived.hasAnnouncement()) {
                                GameMessage.AnnouncementMsg announcement = msgReceived.getAnnouncement();
                                List<GameAnnouncement> gamesInMsg = announcement.getGamesList();
                                for (GameAnnouncement game : gamesInMsg) {
                                    GameAndMasterAddress gameRecord = new GameAndMasterAddress(game, source);
                                    boolean containsSame = false;
                                    for (GameAndMasterAddress curGame : games) {
                                        if ((curGame.address().equals(gameRecord.address()))
                                                && curGame.game().getGameName().equals(game.getGameName())) {
                                            containsSame = true;
                                            break;
                                        }
                                    }
                                    if (!containsSame) {
                                        games.add(gameRecord);
                                        listModified = true;
                                    }
                                }
                            }
                        }
                    }
                    selectedKeys.clear();
                }
            }
            this.selector = null;
        }
        catch (IOException e ) {
            clientUI.notifyOnError("Something went wrong: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void newGame() {
        try {
            InputStream configStream =
                    ClassLoader.getSystemResourceAsStream(CONFIG_FILENAME);
            if (null == configStream) {
                throw new IOException("Cannot open file: " + CONFIG_FILENAME);
            }
            currentState = null;
            currentStateBuilder = null;
            config = parseConfig(configStream);
            unacknowledgedMessages.clear();
            unacknowledgedMessagesToMaster.clear();
            lastReceivingMomentsForPlayers.clear();
            lastSendingMomentsForPlayers.clear();
            addrToIdMap.clear();
            msgSeq = 1;
            id = 0;
            maxIdInGame = 0;
            role = NodeRole.MASTER;
            masterId = id;
            deputyId = id;
            playAsMaster();
        }
        catch (IOException e) {
            clientUI.notifyOnError("Something went wrong: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void joinGame(GameAndMasterAddress gameAndMasterAddress, NodeRole requestingRole) {
        try {
            config = gameAndMasterAddress.game().getConfig();
            role = requestingRole;
            msgSeq = 1;
            currentStateBuilder = null;
            currentState = null;
            masterAddress = gameAndMasterAddress.address();
            unacknowledgedMessages.clear();
            unacknowledgedMessagesToMaster.clear();
            lastReceivingMomentsForPlayers.clear();
            lastSendingMomentsForPlayers.clear();
            addrToIdMap.clear();
            GameMessage joinMsg = GameMessage.newBuilder()
                    .setMsgSeq(msgSeq)
                    .setJoin(GameMessage.JoinMsg.newBuilder()
                            .setPlayerType(PlayerType.HUMAN)
                            .setPlayerName(playerName)
                            .setGameName(gameAndMasterAddress.game().getGameName())
                            .setRequestedRole(requestingRole)
                            .build())
                    .build();
            long msgSeqSent = sendMsg(mainChannel, joinMsg, masterAddress);
            masterId = getMasterId(gameAndMasterAddress.game());
            deputyId = getDeputyId(gameAndMasterAddress.game());
            deputyAddress = getAddress(gameAndMasterAddress.game(), deputyId);
            addrToIdMap.put(masterAddress, masterId);
            addrToIdMap.put(deputyAddress, deputyId);
            ByteBuffer msgReceivingBuffer = ByteBuffer.allocate(MAX_MESSAGE_LENGTH_BYTES);
            String errorDesc = null;
            boolean gotResponse = false;
            boolean gotAck = false;
            try (Selector selector = Selector.open()) {
                this.selector = selector;
                mainChannel.register(selector, SelectionKey.OP_READ);
                while ((!gotResponse) && !Thread.currentThread().isInterrupted()) {
                    selector.select();
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    for (SelectionKey key : selectedKeys) {
                        if ((key.isReadable()) && (Objects.equals(key.channel(), mainChannel))) {
                            mainChannel.receive(msgReceivingBuffer);
                            msgReceivingBuffer.flip();
                            GameMessage msgReceived = GameMessage.parseFrom(msgReceivingBuffer);
                            msgReceivingBuffer.clear();
                            if (checkAckMsg(msgReceived, msgSeqSent, masterId)) {
                                id = msgReceived.getReceiverId();
                                gotResponse = true;
                                gotAck = true;
                                break;
                            } else if (msgReceived.hasError()) {
                                gotResponse = true;
                                errorDesc = msgReceived.getError().getErrorMessage();
                                break;
                            }
                        }
                    }
                    selectedKeys.clear();
                }
            }
            this.selector = null;
            if (!gotAck) {
                clientUI.notifyOnError(errorDesc);
                return;
            }
            playAsNode();
        }
        catch (IOException e) {
            clientUI.notifyOnError("Something went wrong: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void putDirectionCommand(Direction direction) {
        directionCommands.add(direction);
        while (selector == null) {
            Thread.onSpinWait();
        }
        selector.wakeup();
    }

    public void invite(SocketAddress address) {
        inviteQueue.add(address);
        while (selector == null) {
            Thread.onSpinWait();
        }
        selector.wakeup();
    }

    private static class MessageTimePair {
        public MessageTimePair(GameMessage msg, long time) {
            this.msg = msg;
            this.time = time;
        }
        public GameMessage msg;
        public long time;
    }

    private void playAsMaster() throws IOException {
        assert (role == NodeRole.MASTER);
        clientUI.notifyOnGameStart();
        unacknowledgedMessagesToMaster.clear();
        lastReceivingMomentsForPlayers.clear();
        ByteBuffer msgReceivingBuffer = ByteBuffer.allocate(MAX_MESSAGE_LENGTH_BYTES);
        long announcementPeriodMillis = 1000;
        long acknowledgementWaitingPeriod = config.getStateDelayMs() / 10;
        long noMessagesAcceptancePeriod = (long)(0.8 * config.getStateDelayMs());
        long minimumActionsTimeLimit = Math.min(announcementPeriodMillis, acknowledgementWaitingPeriod);
        long selectionTimeMillis;
        initGameState();
        long lastStateChangeMomentMillis = System.currentTimeMillis();
        long lastMulticastAnnouncementMomentMillis = 0;
        Map<Integer, Long> steeringOrder = new HashMap<>();
        gameName = playerName + "'s game";
        clientUI.notifyOnStateChange(config.getWidth(), config.getHeight(),
                                    currentStateBuilder.build(),
                                    mapCurrentIdsToFullSnakesCoords(currentStateBuilder.build()));
        try (Selector selector = Selector.open()) {
            this.selector = selector;
            sendMsg(mainChannel, createAnnouncementMsg(), multicastGroupSocketAddress);
            lastMulticastAnnouncementMomentMillis = System.currentTimeMillis();
            multicastReceivingChannel.register(selector, SelectionKey.OP_READ);
            mainChannel.register(selector, SelectionKey.OP_READ);
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    passMasterPrivileges();
                    break;
                }
                selector.select(minimumActionsTimeLimit);
                selectionTimeMillis = System.currentTimeMillis();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                for (SelectionKey key : selectedKeys) {
                    if ((key.isReadable()) && (Objects.equals(key.channel(), multicastReceivingChannel))) {
                        SocketAddress source = multicastReceivingChannel.receive(msgReceivingBuffer);
                        msgReceivingBuffer.flip();
                        GameMessage msgReceived = GameMessage.parseFrom(msgReceivingBuffer);
                        msgReceivingBuffer.clear();
                        if (msgReceived.hasDiscover()) {
                            sendMsg(mainChannel, createAnnouncementMsg(), source);
                        }
                    }
                    if ((key.isReadable()) && (Objects.equals(key.channel(), mainChannel))) {
                        SocketAddress source = mainChannel.receive(msgReceivingBuffer);
                        msgReceivingBuffer.flip();
                        GameMessage msgReceived = GameMessage.parseFrom(msgReceivingBuffer);
                        msgReceivingBuffer.clear();
                        if (msgReceived.hasJoin()) {
                            GameMessage.JoinMsg joinMsg = msgReceived.getJoin();
                            Integer newPlayerId = addPlayer(joinMsg, (InetSocketAddress) source);
                            if (null == newPlayerId) {
                                GameMessage errorMsg = createErrorMsg("Impossible to join: no place");
                                sendMsg(mainChannel, errorMsg, source);
                                addUnacknowledgedMessage(source, new MessageTimePair(errorMsg, System.currentTimeMillis()));
                            }
                            else {
                                lastReceivingMomentsForPlayers.put(newPlayerId, selectionTimeMillis);
                                sendAckMsg(mainChannel, msgReceived.getMsgSeq(), newPlayerId, source);
                                lastSendingMomentsForPlayers.put(newPlayerId, System.currentTimeMillis());
                                addrToIdMap.put(source, newPlayerId);
                            }
                        }
                        if (msgReceived.hasSteer()) {
                            GameMessage.SteerMsg steerMsg = msgReceived.getSteer();
                            int playerId = addrToIdMap.get(source);
                            Long lastSteering = steeringOrder.get(playerId);
                            if ((lastSteering == null) || (msgReceived.getMsgSeq() > lastSteering)) {
                                applySteering(steerMsg, playerId);
                                lastReceivingMomentsForPlayers.put(playerId, selectionTimeMillis);
                                sendAckMsg(mainChannel, msgReceived.getMsgSeq(), playerId, source);
                                lastSendingMomentsForPlayers.put(playerId, System.currentTimeMillis());
                                steeringOrder.put(playerId, msgReceived.getMsgSeq());
                            }
                        }
                        if (msgReceived.hasPing()) {
                            int playerId = addrToIdMap.get(source);
                            lastReceivingMomentsForPlayers.put(playerId, selectionTimeMillis);
                            sendAckMsg(mainChannel, msgReceived.getMsgSeq(), playerId, source);
                            lastSendingMomentsForPlayers.put(playerId, System.currentTimeMillis());
                        }
                        if (msgReceived.hasAck()) {
                            if (addrToIdMap.containsKey(source)) {
                                if (isNotEquals(selectionTimeMillis, source, msgReceived)) {
                                    continue;
                                }
                            }
                        }
                        if (msgReceived.hasRoleChange()) {
                            Integer playerId = addrToIdMap.get(source);
                            if ((playerId == null) || (playerId != msgReceived.getSenderId())) {
                                continue;
                            }
                            GamePlayer player = null;
                            List<GamePlayer> playersList = currentStateBuilder.getPlayers().getPlayersList();
                            for (GamePlayer curPlayer : playersList) {
                                if (curPlayer.getId() == playerId) {
                                    player = curPlayer;
                                    break;
                                }
                            }
                            if (player == null) {
                                throw new RuntimeException("Msg for quitting of non-existing player");
                            }
                            GameMessage.RoleChangeMsg roleChangeMsg = msgReceived.getRoleChange();
                            if (roleChangeMsg.hasSenderRole()) {
                                if (roleChangeMsg.getSenderRole() == NodeRole.VIEWER) {
                                    if (player.getRole() == NodeRole.DEPUTY) {
                                        appointDeputyOtherThan(playerId);
                                    }
                                    changeRole(playerId, NodeRole.VIEWER);
                                    makeSnakeZombie(playerId);
                                }
                            }
                            if (roleChangeMsg.hasReceiverRole()) {
                                if (roleChangeMsg.getReceiverRole() == NodeRole.VIEWER) {
                                    if (player.getRole() == NodeRole.DEPUTY) {
                                        clientUI.notifyOnError("Seems we lost connection for too long and were kicked");
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                selectedKeys.clear();
                Direction nextCommand = directionCommands.poll();
                if (null != nextCommand) {
                    applySteering(GameMessage.SteerMsg.newBuilder().setDirection(nextCommand).build(), id);
                }
                SocketAddress nextToInvite = inviteQueue.poll();
                if (null != nextToInvite) {
                    GameMessage announcementMsg = createAnnouncementMsg();
                    sendMsg(mainChannel, announcementMsg, nextToInvite);
                }
                handleDroppedAsMaster(selectionTimeMillis, noMessagesAcceptancePeriod);
                resendUnacknowledgedForTooLong(selectionTimeMillis,
                                                acknowledgementWaitingPeriod);
                if (System.currentTimeMillis() - lastStateChangeMomentMillis > config.getStateDelayMs()) {
                    updateState();
                    lastStateChangeMomentMillis = System.currentTimeMillis();
                    broadcastNewState();
                    clientUI.notifyOnStateChange(config.getWidth(), config.getHeight(),
                                                currentStateBuilder.build(),
                                                mapCurrentIdsToFullSnakesCoords(currentStateBuilder.build()));
                    informNewlyDied();
                    newlyDiedToInform.clear();
                }
                if (System.currentTimeMillis() - lastMulticastAnnouncementMomentMillis > announcementPeriodMillis) {
                    GameMessage announcementMsg = createAnnouncementMsg();
                    sendMsg(mainChannel, announcementMsg, multicastGroupSocketAddress);
                    lastMulticastAnnouncementMomentMillis = System.currentTimeMillis();
                }
                if (deputyId == masterId) {
                    appointDeputyOtherThan(null);
                }
                pingIfNecessary(selectionTimeMillis, acknowledgementWaitingPeriod);
            }
        }
        selector = null;
    }

    private boolean isNotEquals(long selectionTimeMillis, SocketAddress source, GameMessage msgReceived) {
        int playerId = addrToIdMap.get(source);
        if (playerId != msgReceived.getSenderId()) {
            return true;
        }
        long ackIsFor = msgReceived.getMsgSeq();
        List<MessageTimePair> messagesList = unacknowledgedMessages.get(source);
        if (messagesList != null) {
            ListIterator<MessageTimePair> messagesListIterator = messagesList.listIterator();
            while (messagesListIterator.hasNext()) {
                MessageTimePair curPair = messagesListIterator.next();
                if (curPair.msg.getMsgSeq() == ackIsFor) {
                    messagesListIterator.remove();
                    break;
                }
            }
        }
        lastReceivingMomentsForPlayers.put(playerId, selectionTimeMillis);
        return false;
    }

    private void playAsNode() throws IOException {
        assert (role != NodeRole.MASTER);
        clientUI.notifyOnGameStart();
        ByteBuffer msgReceivingBuffer = ByteBuffer.allocate(MAX_MESSAGE_LENGTH_BYTES);
        long acknowledgementWaitingPeriod = config.getStateDelayMs() / 10;
        long noMessagesAcceptancePeriod = (long)(0.8 * config.getStateDelayMs());
        long selectionTimeMillis;
        if (currentStateBuilder != null) {
            currentState = currentStateBuilder.build();
            clientUI.notifyOnStateChange(config.getWidth(), config.getHeight(),
                                        currentState, mapCurrentIdsToFullSnakesCoords(currentState));
        }
        boolean turnedToMaster = false;
        try (Selector selector = Selector.open()) {
            this.selector = selector;
            mainChannel.register(selector, SelectionKey.OP_READ);
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    role = NodeRole.VIEWER;
                    sendRoleChangeMsg(masterId, null, NodeRole.VIEWER);
                    break;
                }
                selector.select(acknowledgementWaitingPeriod);
                selectionTimeMillis = System.currentTimeMillis();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                for (SelectionKey key : selectedKeys) {
                    if ((key.isReadable()) && (Objects.equals(key.channel(), mainChannel))) {
                        SocketAddress source = mainChannel.receive(msgReceivingBuffer);
                        msgReceivingBuffer.flip();
                        GameMessage msgReceived = GameMessage.parseFrom(msgReceivingBuffer);
                        msgReceivingBuffer.clear();
                        if (msgReceived.hasPing()) {
                            int playerId = addrToIdMap.get(source);
                            if (masterId != playerId) {
                                continue;
                            }
                            lastReceivingMomentsForPlayers.put(playerId, selectionTimeMillis);
                            sendAckMsg(mainChannel, msgReceived.getMsgSeq(), playerId, source);
                            lastSendingMomentsForPlayers.put(playerId, System.currentTimeMillis());
                        }
                        if (msgReceived.hasAck()) {
                            if (isNotEquals(selectionTimeMillis, source, msgReceived)) {
                                continue;
                            }
                        }
                        if (msgReceived.hasState()) {
                            GameState stateGot = msgReceived.getState().getState();
                            int playerId = addrToIdMap.get(source);
                            if (masterId != playerId) {
                                continue;
                            }
                            if ((currentState == null) || (currentState.getStateOrder() < stateGot.getStateOrder())) {
                                List<GamePlayer> playersList = stateGot.getPlayers().getPlayersList();
                                for (GamePlayer curPlayer : playersList) {
                                    if (curPlayer.getRole() == NodeRole.DEPUTY) {
                                        deputyId = curPlayer.getId();
                                        break;
                                    }
                                }
                                deputyAddress = getPlayerAddress(stateGot, deputyId);
                                currentState = stateGot;
                                clientUI.notifyOnStateChange(config.getWidth(), config.getHeight(),
                                        currentState, mapCurrentIdsToFullSnakesCoords(currentState));
                            }
                            lastReceivingMomentsForPlayers.put(playerId, selectionTimeMillis);
                            sendAckMsg(mainChannel, msgReceived.getMsgSeq(), playerId, source);
                            lastSendingMomentsForPlayers.put(playerId, System.currentTimeMillis());
                        }
                        if (msgReceived.hasRoleChange()) {
                            int playerId = msgReceived.getSenderId();
                            addrToIdMap.put(source, playerId);
                            GameMessage.RoleChangeMsg roleChangeMsg = msgReceived.getRoleChange();
                            if (roleChangeMsg.hasSenderRole()) {
                                if ((playerId == deputyId) && roleChangeMsg.getSenderRole() == NodeRole.MASTER) {
                                    masterId = deputyId;
                                    masterAddress = source;
                                }
                            }
                            if (roleChangeMsg.hasReceiverRole()) {
                                if ((playerId == masterId) && (roleChangeMsg.getReceiverRole() == NodeRole.DEPUTY)) {
                                    deputyId = id;
                                    role = NodeRole.DEPUTY;
                                }
                                if ((role == NodeRole.DEPUTY) && (playerId == masterId) &&
                                        roleChangeMsg.getReceiverRole() == NodeRole.MASTER) {
                                    turnedToMaster = true;
                                    break;
                                }
                                if ((playerId == masterId) && (roleChangeMsg.getReceiverRole() == NodeRole.VIEWER)) {
                                    clientUI.notifyOnError("Master thinks we're dead");
                                }
                            }
                        }
                    }
                }
                selectedKeys.clear();
                sendSteerMsgIfCan();
                handleDroppedAsNode(selectionTimeMillis, noMessagesAcceptancePeriod);
                resendUnacknowledgedForTooLongToMaster(selectionTimeMillis,
                        acknowledgementWaitingPeriod);
                pingIfNecessaryAsNode(selectionTimeMillis, acknowledgementWaitingPeriod);
            }
        }
        this.selector = null;
        if (turnedToMaster) {
            turnToMaster();
        }
    }

    private void sendSteerMsgIfCan() throws IOException {
        Direction newDirection = directionCommands.poll();
        if (newDirection == null) {
            return;
        }
        GameMessage steerMsg = GameMessage.newBuilder()
                .setMsgSeq(msgSeq)
                .setSteer(GameMessage.SteerMsg.newBuilder()
                        .setDirection(newDirection))
                .build();
        sendMsg(mainChannel, steerMsg, masterAddress);
        long time = System.currentTimeMillis();
        lastSendingMomentsForPlayers.put(masterId, time);
        addUnacknowledgedMessageToMaster(steerMsg, time);
    }

    private void passMasterPrivileges() throws IOException {
        if (deputyId == id) {
            if (id == appointDeputyOtherThan(null)) {
                return;
            }
        }
        role = NodeRole.VIEWER;
        sendRoleChangeMsg(deputyId, NodeRole.MASTER, NodeRole.VIEWER);
    }

    private void turnToMaster() throws IOException {
        if (masterId != id) {
            sendRoleChangeMsg(masterId, NodeRole.MASTER, NodeRole.VIEWER);
        }
        broadcastMasterStatus();
        changeRole(id, NodeRole.MASTER);
        if (masterId != id){
            changeRole(masterId, NodeRole.VIEWER);
        }
        makeSnakeZombie(masterId);
        masterId = id;
        role = NodeRole.MASTER;
        playAsMaster();
    }

    private void broadcastMasterStatus() throws IOException {
        List<GamePlayer> players = currentState.getPlayers().getPlayersList();
        for (GamePlayer player : players) {
            int playerId = player.getId();
            if (playerId == masterId) {
                continue;
            }
            sendRoleChangeMsg(playerId, null, NodeRole.MASTER);
        }
    }

    private void broadcastNewState() throws IOException {
        List<GamePlayer> players = currentStateBuilder.getPlayers().getPlayersList();
        GameMessage.Builder stateMessageBuilder = GameMessage.newBuilder()
                .setState(GameMessage.StateMsg.newBuilder()
                        .setState(currentStateBuilder));
        for (GamePlayer player : players) {
            int playerId = player.getId();
            if (playerId == id) {
                continue;
            }
            if (player.getPort() == 0) {
                continue;
            }
            InetSocketAddress playerAddress = new InetSocketAddress(player.getIpAddress(), player.getPort());
            GameMessage stateMessage = stateMessageBuilder.setMsgSeq(msgSeq).build();
            sendMsg(mainChannel, stateMessage, playerAddress);
            long time = System.currentTimeMillis();
            lastSendingMomentsForPlayers.put(playerId, time);
            addUnacknowledgedMessage(playerAddress, new MessageTimePair(stateMessage, time));
        }
    }

    private void informNewlyDied() throws IOException {
        for (GamePlayer player : newlyDiedToInform) {
            int playerId = player.getId();
            if (id == playerId) {
                continue;
            }
            GameMessage roleChangeMessage = GameMessage.newBuilder()
                    .setMsgSeq(msgSeq)
                    .setSenderId(id)
                    .setReceiverId(playerId)
                    .setRoleChange(GameMessage.RoleChangeMsg.newBuilder()
                            .setReceiverRole(NodeRole.VIEWER)).build();
            if (player.getPort() == 0) {
                continue;
            }
            SocketAddress address = new InetSocketAddress(player.getIpAddress(), player.getPort());
            sendMsg(mainChannel, roleChangeMessage, address);
            long time = System.currentTimeMillis();
            lastSendingMomentsForPlayers.put(playerId, time);
            addUnacknowledgedMessage(address, new MessageTimePair(roleChangeMessage, time));
        }
    }

    private void pingIfNecessaryAsNode(long forTime, long limit) throws IOException {
        Set<Map.Entry<Integer, Long>> lastSendingsSet = lastSendingMomentsForPlayers.entrySet();
        List<Integer> toRemove = new ArrayList<>();
        for (Map.Entry<Integer, Long> entry : lastSendingsSet) {
            int playerId = entry.getKey();
            if ((forTime - entry.getValue()) > limit) {
                if (playerId == masterId) {
                    GameMessage pingMsg = GameMessage.newBuilder()
                            .setMsgSeq(msgSeq)
                            .setPing(GameMessage.PingMsg.newBuilder().build())
                            .build();
                    sendMsg(mainChannel, pingMsg, masterAddress);
                    long sendingMoment = System.currentTimeMillis();
                    addUnacknowledgedMessageToMaster(pingMsg, sendingMoment);
                    lastSendingMomentsForPlayers.put(playerId, sendingMoment);
                }
                else {
                    toRemove.add(playerId);
                }
            }
        }
        for (Integer id : toRemove) {
            lastSendingMomentsForPlayers.remove(id);
        }
    }

    private void pingIfNecessary(long forTime, long limit) throws IOException {
        Set<Map.Entry<Integer, Long>> lastSendingsSet = lastSendingMomentsForPlayers.entrySet();
        List<Integer> toRemove = new ArrayList<>();
        for (Map.Entry<Integer, Long> entry : lastSendingsSet) {
            int playerId = entry.getKey();
            if (playerId == id) {
                continue;
            }
            if ((forTime - entry.getValue()) > limit) {
                GamePlayer player = null;
                for (GamePlayer curPlayer : currentStateBuilder.getPlayers().getPlayersList()) {
                    if (curPlayer.getId() == playerId) {
                        player = curPlayer;
                        break;
                    }
                }
                if (player == null) {
                    toRemove.add(playerId);
                    continue;
                }
                GameMessage pingMsg = GameMessage.newBuilder()
                        .setMsgSeq(msgSeq)
                        .setPing(GameMessage.PingMsg.newBuilder().build())
                        .build();
                if (player.getPort() == 0) {
                    continue;
                }
                InetSocketAddress address = new InetSocketAddress(player.getIpAddress(), player.getPort());
                sendMsg(mainChannel, pingMsg, address);
                long sendingMoment = System.currentTimeMillis();
                addUnacknowledgedMessage(address, new MessageTimePair(pingMsg, sendingMoment));
                lastSendingMomentsForPlayers.put(playerId, sendingMoment);
            }
        }
        for (Integer id : toRemove) {
            lastSendingMomentsForPlayers.remove(id);
        }
    }

    private Map<Integer, List<GameState.Coord>> mapCurrentIdsToFullSnakesCoords(GameState state) {
        Map<Integer, List<GameState.Coord>> result = new HashMap<>();
        List<GameState.Snake> snakes = state.getSnakesList();
        for (GameState.Snake snake : snakes) {
            List<GameState.Coord> curCoords = getFullPointsList(snake);
            result.put(snake.getPlayerId(), curCoords);
        }
        return result;
    }

    private int appointDeputyOtherThan(Integer playerIdRequiredNotToBeDeputy) throws IOException {
        int newDeputyId = 0;
        GamePlayer newDeputy = null;
        int masterId = 0;
        List<GamePlayer> playersList = currentStateBuilder.getPlayers().getPlayersList();
        for (GamePlayer curPlayer : playersList) {
            if (curPlayer.getRole() == NodeRole.MASTER) {
                masterId = curPlayer.getId();
            }
            if ((curPlayer.getRole() == NodeRole.NORMAL) &&
                    (!Integer.valueOf(curPlayer.getId()).equals(playerIdRequiredNotToBeDeputy))) {
                newDeputyId = curPlayer.getId();
                newDeputy = curPlayer;
            }
        }
        if (newDeputy == null) {
            deputyId = masterId;
        }
        else {
            changeRole(newDeputyId, NodeRole.DEPUTY);
            sendRoleChangeMsg(newDeputyId, NodeRole.DEPUTY, null);
            if (playerIdRequiredNotToBeDeputy != null) {
                changeRole(playerIdRequiredNotToBeDeputy, NodeRole.NORMAL);
            }
            deputyId = newDeputyId;
        }
        return deputyId;
    }

    private void handleDroppedAsMaster(long forTime, long limit) throws IOException {
        Set<Map.Entry<Integer, Long>> momentsSet = lastReceivingMomentsForPlayers.entrySet();
        List<Integer> toRemove = new ArrayList<>();
        for (Map.Entry<Integer, Long> entry: momentsSet) {
            if ((forTime - entry.getValue()) > limit) {
                int playerId = entry.getKey();
                if (playerId == id) {
                    continue;
                }
                GamePlayer player = null;
                for (GamePlayer curPlayer : currentStateBuilder.getPlayers().getPlayersList()) {
                    if (curPlayer.getId() == playerId) {
                        player = curPlayer;
                        break;
                    }
                }
                if (player == null) {
                    toRemove.add(playerId);
                    continue;
                }
                if (player.getRole() == NodeRole.NORMAL) {
                    changeRole(playerId, NodeRole.VIEWER);
                    sendRoleChangeMsg(playerId, NodeRole.VIEWER, null);
                }
                if (player.getRole() == NodeRole.DEPUTY) {
                    changeRole(playerId, NodeRole.VIEWER);
                    sendRoleChangeMsg(playerId, NodeRole.VIEWER, null);
                    appointDeputyOtherThan(playerId);
                }
            }
        }
        for (Integer curId : toRemove) {
            lastReceivingMomentsForPlayers.remove(curId);
        }
    }

    private void handleDroppedAsNode(long forTime, long limit) throws IOException {
        Set<Map.Entry<Integer, Long>> momentsSet = lastReceivingMomentsForPlayers.entrySet();
        List<Integer> toRemove = new ArrayList<>();
        for (Map.Entry<Integer, Long> entry: momentsSet) {
            if ((forTime - entry.getValue()) > limit) {
                int playerId = entry.getKey();
                if (playerId == id) {
                    continue;
                }
                if (playerId == masterId) {
                    if ((role == NodeRole.NORMAL) || (role == NodeRole.VIEWER)) {
                        masterId = deputyId;
                        masterAddress = deputyAddress;
                    }
                    if (role == NodeRole.DEPUTY) {
                        turnToMaster();
                    }
                }
                else {
                    toRemove.add(entry.getKey());
                }
            }
        }
        for (Integer curId : toRemove) {
            lastReceivingMomentsForPlayers.remove(curId);
        }
    }

    private void changeRole(int playerId, NodeRole newRole) {
        int playerNumberInList = 0;
        GamePlayer player = null;
        List<GamePlayer> playersList = (role == NodeRole.MASTER) ? currentStateBuilder.getPlayers().getPlayersList()
                : currentState.getPlayers().getPlayersList();
        for (int i = 0; i < playersList.size(); ++i) {
            GamePlayer curPlayer = playersList.get(i);
            if (curPlayer.getId() == playerId) {
                playerNumberInList = i;
                player = curPlayer;
                break;
            }
        }
        if (player == null) {
            throw new RuntimeException("Attempt to change role of a non-existing player?");
        }
        GamePlayer updated = player.toBuilder()
                .setRole(newRole)
                .build();
        if (role == NodeRole.MASTER) {
            currentStateBuilder.setPlayers(currentStateBuilder.getPlayersBuilder()
                    .setPlayers(playerNumberInList, updated));
        }
        else {
            currentState = currentState.toBuilder().setPlayers(currentState.getPlayers().toBuilder()
                    .setPlayers(playerNumberInList, updated).build()).build();
        }
        if ((playerId == id) && (newRole != NodeRole.MASTER)) {
            role = newRole;
        }
    }

    private void sendRoleChangeMsg(int destId, NodeRole remoteRole, NodeRole myRole) throws IOException {
        assert ((remoteRole != null) || (myRole != null));
        GameMessage.Builder roleChangeMsgBuilder = GameMessage.newBuilder()
                .setMsgSeq(msgSeq)
                .setSenderId(id)
                .setReceiverId(destId);
        GameMessage.RoleChangeMsg.Builder innerMsgBuilder = GameMessage.RoleChangeMsg.newBuilder();
        if (remoteRole != null) {
            innerMsgBuilder.setReceiverRole(remoteRole);
        }
        if (myRole != null) {
            innerMsgBuilder.setSenderRole(myRole);
        }
        GamePlayer player = null;
        List<GamePlayer> players = (id == masterId) ? currentStateBuilder.getPlayers().getPlayersList()
                            : currentState.getPlayers().getPlayersList();
        for (GamePlayer curPlayer : players) {
            if (curPlayer.getId() == destId) {
                player = curPlayer;
                break;
            }
        }
        if (player == null) {
            return;
        }
        GameMessage roleChangeMsg = roleChangeMsgBuilder.setRoleChange(innerMsgBuilder).build();
        SocketAddress playerAddress = (destId == masterId) ? masterAddress
                : new InetSocketAddress(player.getIpAddress(), player.getPort());
        if ((destId != masterId) && (player.getPort() == 0)) {
            return;
        }
        sendMsg(mainChannel, roleChangeMsg, playerAddress);
        long sendingMoment = System.currentTimeMillis();
        addUnacknowledgedMessage(playerAddress, new MessageTimePair(roleChangeMsg, sendingMoment));
        lastSendingMomentsForPlayers.put(destId, sendingMoment);
    }

    private void resendUnacknowledgedForTooLongToMaster(long forTime, long limit) throws IOException {
        Set<Map.Entry<GameMessage, Long>> messagesSet = unacknowledgedMessagesToMaster.entrySet();
        for (Map.Entry<GameMessage, Long> entry : messagesSet) {
            if ((forTime - entry.getValue()) > limit) {
                sendMsg(mainChannel, entry.getKey(), masterAddress);
                lastSendingMomentsForPlayers.put(masterId, System.currentTimeMillis());
            }
        }
    }

    private void resendUnacknowledgedForTooLong(long forTime, long limit) throws IOException {
        Set<Map.Entry<SocketAddress, List<MessageTimePair>>> messagesSet = unacknowledgedMessages.entrySet();
        for (Map.Entry<SocketAddress, List<MessageTimePair>> entry : messagesSet) {
            for (MessageTimePair messageTimePair : entry.getValue()) {
                if ((forTime - messageTimePair.time) > limit) {
                    sendMsg(mainChannel, messageTimePair.msg, entry.getKey());
                    lastSendingMomentsForPlayers.put(addrToIdMap.get(entry.getKey()), System.currentTimeMillis());
                }
            }
        }
    }

    private void applySteering(GameMessage.SteerMsg steerMsg, int id) {
        Direction direction = steerMsg.getDirection();
        List<GameState.Snake> snakes = currentStateBuilder.getSnakesList();
        int idxOfId = 0;
        for (int i = 0; i < snakes.size(); ++i) {
            GameState.Snake curSnake = snakes.get(i);
            if (curSnake.getPlayerId() == id) {
                if (directionsAreOpposite(direction, curSnake.getHeadDirection())) {
                    return;
                }
                idxOfId = i;
                break;
            }
        }
        currentStateBuilder.getSnakesBuilder(idxOfId).setHeadDirection(direction);
    }

    private boolean directionsAreOpposite(Direction first, Direction second) {
        switch (first) {
            case UP -> {
                return (second == Direction.DOWN);
            }
            case DOWN -> {
                return (second == Direction.UP);
            }
            case LEFT -> {
                return (second == Direction.RIGHT);
            }
            case RIGHT -> {
                return (second == Direction.LEFT);
            }
        }
        return false;
    }

    private void updateState() {
        newlyDiedToInform.clear();
        List<GameState.Snake> newSnakes = new ArrayList<>();
        List<GameState.Snake> oldSnakes = currentStateBuilder.getSnakesList();
        for (GameState.Snake curOldSnake : oldSnakes) {
            newSnakes.add(moveHead(curOldSnake));
        }
        List<GameState.Coord> oldFoods = currentStateBuilder.getFoodsList();
        List<GameState.Coord> newFoods = new ArrayList<>(oldFoods);
        List<Integer> playersIdsToIncrementScoresForEating = new ArrayList<>();
        for (int i = 0; i < newSnakes.size(); ++i) {
            GameState.Snake curNewSnake = newSnakes.get(i);
            GameState.Coord curNewSnakeHead = curNewSnake.getPoints(0);
            if (!oldFoods.contains(curNewSnakeHead)) {
                GameState.Snake updatedNewSnake = moveTail(curNewSnake);
                newSnakes.set(i, updatedNewSnake);
            }
            else {
                playersIdsToIncrementScoresForEating.add(curNewSnake.getPlayerId());
                newFoods.remove(curNewSnakeHead);
            }
        }
        List<GameState.Snake> dyingSnakes = new ArrayList<>();
        List<Integer> dyingPlayers = new ArrayList<>();
        List<List<GameState.Coord>> fullPointsLists = new ArrayList<>();
        List<Integer> playersIdsToIncrementScoresForBeingVictims = new ArrayList<>();
        for (GameState.Snake curNewSnake : newSnakes) {
            fullPointsLists.add(getFullPointsList(curNewSnake));
        }
        for (int i = 0; i < newSnakes.size(); ++i) {
            GameState.Snake curSnake = newSnakes.get(i);
            boolean alreadyDies = false;
            for (int j = 0; j < fullPointsLists.size(); ++j) {
                List<GameState.Coord> possibleVictimPoints = fullPointsLists.get(j);
                int crashPointIdx = (i != j) ? possibleVictimPoints.indexOf(curSnake.getPoints(0))
                        : possibleVictimPoints.lastIndexOf(curSnake.getPoints(0));
                if (-1 != crashPointIdx) {
                    if (i != j || crashPointIdx != 0) {
                        //Not head of self
                        if (!alreadyDies) {
                            dyingSnakes.add(curSnake);
                            dyingPlayers.add(i);
                            alreadyDies = true;
                        }
                        playersIdsToIncrementScoresForBeingVictims.add(j);
                    }
                }
            }
        }
        playersIdsToIncrementScoresForBeingVictims.removeAll(dyingPlayers);
        List<GamePlayer> newPlayers = new ArrayList<>();
        List<GamePlayer> oldPlayers = currentStateBuilder.getPlayers().getPlayersList();
        for (GamePlayer curOldPlayer : oldPlayers) {
            int playerId = curOldPlayer.getId();
            boolean changed = false;
            int newScore = curOldPlayer.getScore();
            NodeRole newRole = curOldPlayer.getRole();
            if (playersIdsToIncrementScoresForEating.contains(playerId)) {
                changed = true;
                newScore++;
                playersIdsToIncrementScoresForEating.remove(Integer.valueOf(playerId));
            }
            if (playersIdsToIncrementScoresForBeingVictims.contains(playerId)) {
                changed = true;
                newScore++;
                playersIdsToIncrementScoresForBeingVictims.remove(Integer.valueOf(playerId));
            }
            if (dyingPlayers.contains(playerId)) {
                changed = true;
                if (playerId != id) {
                    newRole = NodeRole.VIEWER;
                    newlyDiedToInform.add(curOldPlayer);
                }
            }
            GamePlayer newPlayerData;
            if (changed) {
                 newPlayerData = GamePlayer.newBuilder()
                        .mergeFrom(curOldPlayer)
                        .setScore(newScore)
                        .setRole(newRole)
                        .build();
            }
            else {
                newPlayerData = curOldPlayer;
            }
            newPlayers.add(newPlayerData);
        }
        newSnakes.removeAll(dyingSnakes);
        int aliveSnakes = 0;
        for (GameState.Snake curNewSnake : newSnakes) {
            if (curNewSnake.getState() == GameState.Snake.SnakeState.ALIVE) {
                aliveSnakes++;
            }
        }
        List<GameState.Coord> foodToAdd = findPlacesForFood(config.getFoodStatic() + aliveSnakes -
                                            newFoods.size());
        newFoods.addAll(foodToAdd);
        for (GameState.Snake curDyingSnake : dyingSnakes) {
            List<GameState.Coord> allPoints = getFullPointsList(curDyingSnake);
            List<GameState.Coord> newFoodFromSnake = randomlyChoosePlacesForFoodFromCoords(allPoints);
            newFoods.addAll(newFoodFromSnake);
        }
        currentStateBuilder
                .setStateOrder(currentStateBuilder.getStateOrder() + 1)
                .clearSnakes()
                .addAllSnakes(newSnakes)
                .clearFoods()
                .addAllFoods(newFoods)
                .clearPlayers()
                .setPlayers(GamePlayers.newBuilder()
                        .addAllPlayers(newPlayers));
    }

    private GameState.Snake moveHead(GameState.Snake snake) {
        List<GameState.Coord> points = new ArrayList<>(snake.getPointsList());
        Direction headDirection = snake.getHeadDirection();
        GameState.Coord oldHeadPlace = points.get(0);
        GameState.Coord newHeadPlace = getShiftedForSinglePoint(oldHeadPlace, headDirection);
        boolean isTurn = determineInvertedDirection(points.get(1)) != headDirection;
        if (isTurn) {
            points.set(0, getShiftedForSinglePointWithoutLimit(GameState.Coord.newBuilder().setX(0).setY(0).build(),
                        invertDirection(headDirection)));
            points.add(0, newHeadPlace);
        }
        else {
            points.set(0, newHeadPlace);
            points.set(1, getShiftedForSinglePointWithoutLimit(points.get(1), invertDirection(headDirection)));
        }
        return GameState.Snake.newBuilder().mergeFrom(snake).clearPoints().addAllPoints(points).build();
    }

    private GameState.Snake moveTail(GameState.Snake snake) {
        List<GameState.Coord> points = new ArrayList<>(snake.getPointsList());
        GameState.Coord oldTailShift = points.get(points.size() - 1);
        Direction direction = determineInvertedDirection(oldTailShift);
        GameState.Coord newTailShift = getShiftedForSinglePointWithoutLimit(oldTailShift, direction);
        boolean becameZero = ((newTailShift.getX() == 0) && (newTailShift.getY() == 0));
        if (becameZero) {
            points.remove(points.size() - 1);
        }
        else {
            points.set(points.size() - 1, newTailShift);
        }
        return GameState.Snake.newBuilder().mergeFrom(snake).clearPoints().addAllPoints(points).build();
    }

    private List<GameState.Coord> randomlyChoosePlacesForFoodFromCoords(List<GameState.Coord> coords) {
        List<GameState.Coord> result = new ArrayList<>();
        for (GameState.Coord curCoords : coords) {
            if (Math.random() > 0.5) {
                result.add(curCoords);
            }
        }
        return result;
    }

    private void makeSnakeZombie(int playerId) {
        int snakeNumberInList = 0;
        boolean found = false;
        List<GameState.Snake> snakesList = (role == NodeRole.MASTER) ? currentStateBuilder.getSnakesList()
                : currentState.getSnakesList();
        for (int i = 0; i < snakesList.size(); ++i) {
            GameState.Snake curSnake = snakesList.get(i);
            if (curSnake.getPlayerId() == playerId) {
                snakeNumberInList = i;
                found = true;
                break;
            }
        }
        if (found) {
            if (role == NodeRole.MASTER) {
                currentStateBuilder.getSnakesBuilder(snakeNumberInList).setState(GameState.Snake.SnakeState.ZOMBIE);
            }
            else {
                currentState = currentState.toBuilder().setSnakes(snakeNumberInList,
                        currentState.getSnakes(snakeNumberInList).toBuilder()
                                .setState(GameState.Snake.SnakeState.ZOMBIE)).build();
            }
        }
    }

    private Integer addPlayer(GameMessage.JoinMsg joinMsg, InetSocketAddress address) {
        GameState stateUpdate;
        if (joinMsg.getRequestedRole() == NodeRole.VIEWER) {
            stateUpdate = GameState.newBuilder()
                    .setStateOrder(currentStateBuilder.getStateOrder())
                    .setPlayers(GamePlayers.newBuilder()
                            .addPlayers(GamePlayer.newBuilder()
                                    .setName(joinMsg.getPlayerName())
                                    .setId(maxIdInGame + 1)
                                    .setIpAddress(address.getHostName())
                                    .setPort(address.getPort())
                                    .setRole(joinMsg.getRequestedRole())
                                    .setScore(0)))
                    .build();
        }
        else {
            List<GameState.Coord> newSnakePosition = findNewSnakePosition();
            if (newSnakePosition == null) {
                return null;
            }
            stateUpdate = GameState.newBuilder()
                    .setStateOrder(currentStateBuilder.getStateOrder())
                    .addSnakes(GameState.Snake.newBuilder()
                            .setPlayerId(maxIdInGame + 1)
                            .addAllPoints(newSnakePosition)
                            .setState(GameState.Snake.SnakeState.ALIVE)
                            .setHeadDirection(determineInitialHeadDirection(newSnakePosition)))
                    .setPlayers(GamePlayers.newBuilder()
                            .addPlayers(GamePlayer.newBuilder()
                                    .setName(joinMsg.getPlayerName())
                                    .setId(maxIdInGame + 1)
                                    .setIpAddress(address.getHostName())
                                    .setPort(address.getPort())
                                    .setRole(joinMsg.getRequestedRole())
                                    .setScore(0)))
                    .build();
        }
        maxIdInGame++;
        currentStateBuilder.mergeFrom(stateUpdate);
        return maxIdInGame;
    }

    private GameMessage createAnnouncementMsg() {
        return GameMessage.newBuilder()
                .setMsgSeq(msgSeq)
                .setAnnouncement(GameMessage.AnnouncementMsg.newBuilder()
                        .addGames(GameAnnouncement.newBuilder()
                                .setPlayers(currentStateBuilder.getPlayers())
                                .setConfig(config)
                                .setGameName(gameName)
                                .build())
                        .build())
                .build();
    }

    private GameMessage createErrorMsg(String desc) {
        return GameMessage.newBuilder()
                .setMsgSeq(msgSeq)
                .setError(GameMessage.ErrorMsg.newBuilder()
                        .setErrorMessage(desc)
                        .build())
                .build();
    }

    private long sendMsg(DatagramChannel channel, GameMessage msg,
                         SocketAddress destination) throws IOException {
        long msgSeqSent = msg.getMsgSeq();
        byte[] msgAsArray = msg.toByteArray();
        ByteBuffer msgAsByteBuffer = ByteBuffer.wrap(msgAsArray);
        int bytesSent = 0;
        while (0 == bytesSent) {
            bytesSent = channel.send(msgAsByteBuffer, destination);
        }
        msgSeq++;
        return msgSeqSent;
    }

    private void sendAckMsg(DatagramChannel channel, long msgSeq,
                            int receiverId, SocketAddress destination) throws IOException {
        GameMessage ackMsg = GameMessage.newBuilder()
                .setMsgSeq(msgSeq)
                .setSenderId(id)
                .setReceiverId(receiverId)
                .setAck(GameMessage.AckMsg.newBuilder().build())
                .build();
        byte[] msgAsArray = ackMsg.toByteArray();
        ByteBuffer msgAsByteBuffer = ByteBuffer.wrap(msgAsArray);
        int bytesSent = 0;
        while (0 == bytesSent) {
            bytesSent = channel.send(msgAsByteBuffer, destination);
        }
    }

    private boolean checkAckMsg(GameMessage msg, long msgSeqFor, int expectedSender) {
        return (msg.hasAck()) && (msg.getMsgSeq() == msgSeqFor) && (msg.getSenderId() == expectedSender);
    }

    private GameConfig parseConfig(InputStream configStream) throws IOException {
        int widthGot;
        int heightGot;
        int foodStaticGot;
        int stateDelayMsGot;
        try (Scanner scanner = new Scanner(configStream)) {
            widthGot = scanner.nextInt();
            heightGot = scanner.nextInt();
            foodStaticGot = scanner.nextInt();
            stateDelayMsGot = scanner.nextInt();
            playerName = scanner.next();
        }
        return GameConfig.newBuilder()
                .setWidth(widthGot)
                .setHeight(heightGot)
                .setFoodStatic(foodStaticGot)
                .setStateDelayMs(stateDelayMsGot)
                .build();
    }

    private int getMasterId(GameAnnouncement game) {
        for (GamePlayer player : game.getPlayers().getPlayersList()) {
            if (player.getRole() == NodeRole.MASTER) {
                return player.getId();
            }
        }
        throw new RuntimeException("No master in announcement?");
    }

    private int getDeputyId(GameAnnouncement game) {
        for (GamePlayer player : game.getPlayers().getPlayersList()) {
            if (player.getRole() == NodeRole.DEPUTY) {
                return player.getId();
            }
        }
        return getMasterId(game);
    }

    private SocketAddress getPlayerAddress(GameState state, int playerId) {
        for (GamePlayer player : state.getPlayers().getPlayersList()) {
            if (player.getId() == playerId) {
                return new InetSocketAddress(player.getIpAddress(), player.getPort());
            }
        }
        throw new RuntimeException("No address of " + playerId + "?");
    }

    private SocketAddress getAddress(GameAnnouncement game, int playerId) {
        for (GamePlayer player : game.getPlayers().getPlayersList()) {
            if (player.getId() == playerId) {
                return new InetSocketAddress(player.getIpAddress(), player.getPort());
            }
        }
        throw new RuntimeException("No address of player " + playerId + " in announcement?");
    }

    private void initGameState() {
        assert (role == NodeRole.MASTER);
        if (currentState != null) {
            currentStateBuilder = currentState.toBuilder();
            List<GamePlayer> players = currentStateBuilder.getPlayers().getPlayersList();
            for (GamePlayer player : players) {
                int playerId = player.getId();
                if (playerId > maxIdInGame) {
                    maxIdInGame = playerId;
                }
            }
            return;
        }
        List<GameState.Coord> placesForFood = findPlacesForFood(config.getFoodStatic() + 1);
        List<GameState.Coord> initialSnakeCoords = findInitialSnakePosition(placesForFood);
        Direction headDirection = determineInitialHeadDirection(initialSnakeCoords);
        currentStateBuilder = GameState.newBuilder()
                .setStateOrder(0)
                .addSnakes(GameState.Snake.newBuilder()
                        .setPlayerId(id)
                        .addAllPoints(initialSnakeCoords)
                        .setState(GameState.Snake.SnakeState.ALIVE)
                        .setHeadDirection(headDirection))
                .addAllFoods(placesForFood)
                .setPlayers(GamePlayers.newBuilder()
                        .addPlayers(GamePlayer.newBuilder()
                                .setName(playerName)
                                .setId(id)
                                .setRole(NodeRole.MASTER)
                                .setType(PlayerType.HUMAN)
                                .setScore(0)));
    }

    private Direction determineInitialHeadDirection(List<GameState.Coord> snake) {
        GameState.Coord tailShift = snake.get(1);
        return determineInvertedDirection(tailShift);
    }

    private Direction determineInvertedDirection(GameState.Coord coords) {
        if (coords.getX() < 0) {
            return Direction.RIGHT;
        }
        else if (coords.getX() > 0) {
            return Direction.LEFT;
        }
        else {
            if (coords.getY() < 0) {
                return Direction.DOWN;
            }
            if (coords.getY() > 0) {
                return Direction.UP;
            }
        }
        throw new RuntimeException("Point with all-zeros shift in invertDirection method?");
    }

    private Direction invertDirection(Direction direction) {
        switch (direction) {
            case LEFT -> {
                return Direction.RIGHT;
            }
            case RIGHT -> {
                return Direction.LEFT;
            }
            case UP -> {
                return Direction.DOWN;
            }
            case DOWN -> {
                return Direction.UP;
            }
        }
        throw new RuntimeException("Unknown direction?");
    }

    private List<GameState.Coord> findPlacesForFood(int amount) {
        List<GameState.Coord> result = new ArrayList<>();
        int foodGenerated = 0;
        while (foodGenerated < amount) {
            double xRelativePosition = Math.random();
            double yRelativePosition = Math.random();
            int foodX = (int)Math.round(config.getWidth() * xRelativePosition);
            int foodY = (int)Math.round(config.getHeight() * yRelativePosition);
            GameState.Coord foodCoords = GameState.Coord.newBuilder()
                    .setX(foodX)
                    .setY(foodY)
                    .build();
            if (result.contains(foodCoords)) {
                continue;
            }
            boolean underSnake = false;
            if (currentStateBuilder != null) {
                if (currentStateBuilder.getFoodsList().contains(foodCoords)) {
                    continue;
                }
                for (GameState.Snake snake : currentStateBuilder.getSnakesList()) {
                    List<GameState.Coord> allSnakePoints = getFullPointsList(snake);
                    if (allSnakePoints.contains(foodCoords)) {
                        underSnake = true;
                        break;
                    }
                }
            }
            if (!underSnake) {
                result.add(foodCoords);
                foodGenerated++;
            }
        }
        return result;
    }

    private List<GameState.Coord> findNewSnakePosition() {
        GameState.Coord squareUpLeftCoords = findEmpty5x5Square();
        if (squareUpLeftCoords == null) {
            return null;
        }
        List<GameState.Coord> result = new ArrayList<>();
        GameState.Coord headCoords = getShifted(squareUpLeftCoords, 2, 2);
        result.add(headCoords);
        List<Integer> possibleShifts = new ArrayList<>();
        possibleShifts.add(-1);
        possibleShifts.add(0);
        possibleShifts.add(1);
        int xShift;
        int yShift;
        while (true) {
            Collections.shuffle(possibleShifts);
            xShift = possibleShifts.get(0);
            if (xShift != 0) {
                if (currentStateBuilder.getFoodsList().contains(getShifted(headCoords, xShift, 0))) {
                    continue;
                }
                yShift = 0;
                break;
            }
            else {
                Collections.shuffle(possibleShifts);
                yShift = possibleShifts.get(0);
                if (yShift == 0) {
                    yShift = possibleShifts.get(1);
                }
                if (!currentStateBuilder.getFoodsList().contains(getShifted(headCoords, 0, yShift))) {
                    break;
                }
            }
        }
        result.add(GameState.Coord.newBuilder().setX(xShift).setY(yShift).build());
        return result;
    }

    private List<GameState.Coord> findInitialSnakePosition(List<GameState.Coord> foodCoords) {
        List<GameState.Coord> placeForSnake = new ArrayList<>();
        GameState.Coord headCoords;
        List<GameState.Coord> result = new ArrayList<>();
        while (true) {
            double xRelativePosition = Math.random();
            double yRelativePosition = Math.random();
            int x = (int) Math.round(config.getWidth() * xRelativePosition);
            int y = (int) Math.round(config.getHeight() * yRelativePosition);
            GameState.Coord curCoords = GameState.Coord.newBuilder()
                    .setX(x)
                    .setY(y)
                    .build();
            for (int i = -1; i <= 1; ++i) {
                if (i == 0) {
                    for (int j = -1; j <= 1; ++j) {
                        if (j == 0) {
                            placeForSnake.add(curCoords);
                        }
                        else {
                            placeForSnake.add(getShifted(curCoords, j, 0));
                        }
                    }
                }
                else {
                    placeForSnake.add(getShifted(curCoords, 0, i));
                }
            }
            if (!foodCoords.containsAll(placeForSnake)) {
                headCoords = curCoords;
                result.add(curCoords);
                break;
            }
            placeForSnake.clear();
        }
        List<Integer> possibleShifts = new ArrayList<>();
        possibleShifts.add(-1);
        possibleShifts.add(0);
        possibleShifts.add(1);
        int xShift;
        int yShift;
        while (true) {
            Collections.shuffle(possibleShifts);
            xShift = possibleShifts.get(0);
            if (xShift != 0) {
                if (foodCoords.contains(getShifted(headCoords, xShift, 0))) {
                    continue;
                }
                yShift = 0;
                break;
            }
            else {
                Collections.shuffle(possibleShifts);
                yShift = possibleShifts.get(0);
                if (yShift == 0) {
                    yShift = possibleShifts.get(1);
                }
                if (!foodCoords.contains(getShifted(headCoords, 0, yShift))) {
                    break;
                }
            }
        }
        result.add(GameState.Coord.newBuilder().setX(xShift).setY(yShift).build());
        return result;
    }

    private GameState.Coord findEmpty5x5Square() {
        int height = config.getHeight();
        int width = config.getWidth();
        boolean[][] occupation = new boolean[height][width];
        for (GameState.Snake snake : currentStateBuilder.getSnakesList()) {
            List<GameState.Coord> allSnakePoints = getFullPointsList(snake);
            for (GameState.Coord curCoords : allSnakePoints) {
                occupation[curCoords.getY()][curCoords.getX()] = true;
            }
        }
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                if (test5x5Emptiness(occupation, x, y)) {
                    return GameState.Coord.newBuilder().setX(x).setY(y).build();
                }
            }
        }
        return null;
    }

    private boolean test5x5Emptiness(boolean[][] occupation, int upLeftX, int upLeftY) {
        for (int y = upLeftY; y < upLeftY + 5; ++y) {
            for (int x = upLeftX; x < upLeftX + 5; ++x) {
                if (occupation[y][x]) {
                    return false;
                }
            }
        }
        return true;
    }

    private List<GameState.Coord> getFullPointsList(GameState.Snake snake) {
        List<GameState.Coord> result = new ArrayList<>();
        List<GameState.Coord> keyPointsList = snake.getPointsList();
        GameState.Coord prevCoords = null;
        for (GameState.Coord curCoords : keyPointsList) {
            if (prevCoords == null) {
                prevCoords = curCoords;
                result.add(curCoords);
                continue;
            }
            for (int i = Math.min(0, curCoords.getY()); i <= Math.max(0, curCoords.getY()); ++i) {
                for (int j = Math.min(0, curCoords.getX()); j <= Math.max(0, curCoords.getX()); ++j) {
                    if ((i == 0) && (j == 0)) {
                        continue;
                    }
                    GameState.Coord shifted = getShifted(prevCoords, j, i);
                    result.add(shifted);
                }
            }
            prevCoords = getShifted(prevCoords, curCoords.getX(), curCoords.getY());
        }
        return result;
    }

    private GameState.Coord getShiftedForSinglePointWithoutLimit(GameState.Coord base, Direction direction) {
        switch (direction) {
            case UP -> {
                return GameState.Coord.newBuilder().setX(base.getX()).setY(base.getY() - 1).build();
            }
            case DOWN -> {
                return GameState.Coord.newBuilder().setX(base.getX()).setY(base.getY() + 1).build();
            }
            case RIGHT -> {
                return GameState.Coord.newBuilder().setX(base.getX() + 1).setY(base.getY()).build();
            }
            case LEFT -> {
                return GameState.Coord.newBuilder().setX(base.getX() - 1).setY(base.getY()).build();
            }
        }
        throw new RuntimeException("Shift requested for unknown direction?");
    }

    private GameState.Coord getShiftedForSinglePoint(GameState.Coord base, Direction direction) {
        switch (direction) {
            case UP -> {
                return getShifted(base, 0, -1);
            }
            case DOWN -> {
                return getShifted(base, 0, 1);
            }
            case RIGHT -> {
                return getShifted(base, 1, 0);
            }
            case LEFT -> {
                return getShifted(base, -1, 0);
            }
        }
        throw new RuntimeException("Shift requested for unknown direction?");
    }

    private GameState.Coord getShifted(GameState.Coord base, int xShift, int yShift) {
        return GameState.Coord.newBuilder()
                .setX(getShiftedX(base.getX(), xShift))
                .setY(getShiftedY(base.getY(), yShift))
                .build();
    }

    private int getShiftedX(int base, int shift) {
        return getShifted(base, shift, config.getWidth());
    }

    private int getShiftedY(int base, int shift) {
        return getShifted(base, shift, config.getHeight());
    }

    private int getShifted(int base, int shift, int constraint) {
        int shiftedUnlimited = base + shift;
        return (shiftedUnlimited < 0) ? (constraint + shiftedUnlimited) :
                (shiftedUnlimited % constraint);
    }

    private void addUnacknowledgedMessage(SocketAddress address, MessageTimePair messageTimePair) {
        List<MessageTimePair> messagesAndTimesPairsList = unacknowledgedMessages.get(address);
        if (messagesAndTimesPairsList == null) {
            messagesAndTimesPairsList = new ArrayList<>();
        }
        boolean alreadyHave = false;
        for (MessageTimePair pair : messagesAndTimesPairsList) {
            if (pair.msg.equals(messageTimePair.msg)) {
                alreadyHave = true;
                pair.time = messageTimePair.time;
                break;
            }
        }
        if (!alreadyHave) {
            messagesAndTimesPairsList.add(messageTimePair);
        }
    }

    private void addUnacknowledgedMessageToMaster(GameMessage msg, long time) {
        unacknowledgedMessagesToMaster.put(msg, time);
    }

    private final ConcurrentLinkedQueue<SocketAddress> inviteQueue;
    private final ConcurrentLinkedQueue<Direction> directionCommands;
    private volatile Selector selector;
    private final DatagramChannel multicastReceivingChannel;
    private final DatagramChannel mainChannel;
    private final SocketAddress multicastGroupSocketAddress;
    private final NotifiableClient clientUI;
    private GameState.Builder currentStateBuilder;
    private GameState currentState;
    private long msgSeq;
    private NodeRole role;
    private GameConfig config;
    private String playerName = "Local player " + (int)(Math.random() * 10);
    private String gameName;
    private int id;
    private int maxIdInGame;
    private int masterId;
    private int deputyId;
    private final Map<SocketAddress, List<MessageTimePair>> unacknowledgedMessages;
    private final Map<GameMessage, Long> unacknowledgedMessagesToMaster;
    private final Map<Integer, Long> lastReceivingMomentsForPlayers;
    private final Map<Integer, Long> lastSendingMomentsForPlayers;
    private final Map<SocketAddress, Integer> addrToIdMap;
    private final List<GamePlayer> newlyDiedToInform;
    private SocketAddress masterAddress;
    private SocketAddress deputyAddress;
    private static final String MULTICAST_GROUP_ADDRESS = "239.192.0.4";
    private static final int MULTICAST_PORT = 9192;
    private static final int MAX_MESSAGE_LENGTH_BYTES = 16384; //16KB
    private static final String CONFIG_FILENAME = "config.txt";
}
