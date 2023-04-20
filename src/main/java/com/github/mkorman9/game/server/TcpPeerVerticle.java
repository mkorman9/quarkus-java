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
                .closeHandler(v -> vertx.undeploy(deploymentID()))
                .exceptionHandler(this::onException)
                .resume();
    }

    @Override
    public void stop() {
        playerRegistry.unregister(context);
    }

    private void onMessage(Buffer message) {
        if (receiveBuffer.length() + message.length() > maxPacketSize) {
            receiveBuffer = Buffer.buffer();
            return;
        }

        receiveBuffer.appendBuffer(message);

        try {
            while (true) {
                var declaredPacketSize = receiveBuffer.getInt(0);
                var receivedBytes = receiveBuffer.length() - 4;

                if (receivedBytes >= declaredPacketSize) {
                    var packet = receiveBuffer.getBuffer(4, 4 + declaredPacketSize);
                    packetHandler.handle(context, packet);
                    receiveBuffer = receiveBuffer.getBuffer(4 + declaredPacketSize, receiveBuffer.length());
                } else {
                    break;
                }
            }
        } catch (IndexOutOfBoundsException e) {
            // ignore
        }
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
