package com.github.mkorman9.game.server;

import com.github.mkorman9.game.dto.PlayerContext;
import com.github.mkorman9.game.service.PacketHandler;
import com.github.mkorman9.game.service.PlayerRegistry;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketException;

public class TcpPeer {
    private static final Logger LOG = LoggerFactory.getLogger(TcpPeer.class);

    private final PacketHandler packetHandler;
    private final PlayerRegistry playerRegistry;
    private final int maxPacketSize;

    private PlayerContext context;
    private Buffer receiveBuffer = Buffer.buffer();

    public TcpPeer(
            PacketHandler packetHandler,
            PlayerRegistry playerRegistry,
            int maxPacketSize
    ) {
        this.packetHandler = packetHandler;
        this.playerRegistry = playerRegistry;
        this.maxPacketSize = maxPacketSize;
    }

    public void start(NetSocket socket) {
        context = playerRegistry.register(socket);

        socket.handler(this::onMessage)
                .closeHandler(v -> onClose())
                .exceptionHandler(this::onException);
    }

    private void onMessage(Buffer buffer) {
        receiveBuffer.appendBuffer(buffer);

        if (receiveBuffer.length() > maxPacketSize) {
            receiveBuffer = Buffer.buffer();
            return;
        }

        try {
            while (true) {
                var packetSize = receiveBuffer.getIntLE(0);
                var chunkSize = receiveBuffer.length() - 4;

                if (chunkSize >= packetSize) {
                    var packet = receiveBuffer.getBuffer(4, 4 + packetSize);
                    packetHandler.handle(context, packet);
                    receiveBuffer = receiveBuffer.getBuffer(4 + packetSize, receiveBuffer.length());
                } else {
                    break;
                }
            }
        } catch (IndexOutOfBoundsException e) {
            // ignore
        }
    }

    private void onClose() {
        playerRegistry.unregister(context);
    }

    private void onException(Throwable t) {
        if (t instanceof SocketException e) {
            if (e.getMessage().contains("Connection reset")) {
                return;
            }
        }

        LOG.error("Error in TCP connection handler", t);
    }
}
