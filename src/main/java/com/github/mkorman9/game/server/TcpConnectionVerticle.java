package com.github.mkorman9.game.server;

import com.github.mkorman9.game.dto.PlayerContext;
import com.github.mkorman9.game.service.PlayerRegistry;
import com.github.mkorman9.game.service.PacketHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketException;

public class TcpConnectionVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(TcpConnectionVerticle.class);

    private final NetSocket socket;
    private final PacketHandler packetHandler;
    private final PlayerRegistry playerRegistry;

    private PlayerContext context;
    private Buffer receiveBuffer = Buffer.buffer();

    public TcpConnectionVerticle(NetSocket socket, PacketHandler packetHandler, PlayerRegistry playerRegistry) {
        this.socket = socket;
        this.packetHandler = packetHandler;
        this.playerRegistry = playerRegistry;
    }

    @Override
    public void start() throws Exception {
        context = playerRegistry.register(socket);

        socket.handler(this::onMessage)
                .closeHandler(v -> onClose())
                .exceptionHandler(this::onException);
    }

    private void onMessage(Buffer buffer) {
        receiveBuffer.appendBuffer(buffer);

        try {
            var packetSize = receiveBuffer.getIntLE(0);
            var chunkSize = receiveBuffer.length() - 4;

            if (chunkSize >= packetSize) {
                var packet = receiveBuffer.getBuffer(4, 4 + packetSize);
                packetHandler.handle(context, packet);
                receiveBuffer = receiveBuffer.getBuffer(4 + packetSize, receiveBuffer.length());
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
