package com.example.fothlab;

import com.example.fothlab.SnakeProto.*;

import java.net.SocketAddress;

public record GameAndMasterAddress(GameAnnouncement game, SocketAddress address) {}
