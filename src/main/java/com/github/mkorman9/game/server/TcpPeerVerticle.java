package com.github.mkorman9.game.server;

import com.github.mkorman9.game.dto.PlayerContext;
import com.github.mkorman9.game.service.PacketHandler;
import com.github.mkorman9.game.service.PlayerRegistry;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketException;

public class TcpPeerVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(TcpPeerVerticle.class);

    private final NetSocket socket;
    private final PacketHandler packetHandler;
    private final PlayerRegistry playerRegistry;
    private final int maxPacketSize;

    private PlayerContext context;
    private Buffer receiveBuffer = Buffer.buffer();

    public TcpPeerVerticle(
            NetSocket socket,
            PacketHandler packetHandler,
            PlayerRegistry playerRegistry,
            int maxPacketSize
    ) {
        this.socket = socket;
        this.packetHandler = packetHandler;
        this.playerRegistry = playerRegistry;
        this.maxPacketSize = maxPacketSize;
    }

    @Override
    public void start() {
        context = playerRegistry.register(socket);

        socket.handler(this::onMessage)
                .closeHandler(v -> onClose())
                .exceptionHandler(this::onException)
                .resume();
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
        vertx.undeploy(deploymentID());
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
