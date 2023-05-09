package com.github.mkorman9.game.server;

import com.github.mkorman9.game.dto.PlayerContext;
import com.github.mkorman9.game.dto.VarInt;
import com.github.mkorman9.game.service.PacketDispatcher;
import com.github.mkorman9.game.service.PacketSender;
import com.github.mkorman9.game.service.PlayerRegistry;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketException;

public class TcpPeer {
    private static final Logger LOG = LoggerFactory.getLogger(TcpPeer.class);

    private final NetSocket socket;
    private final PacketDispatcher packetDispatcher;
    private final PlayerRegistry playerRegistry;
    private final int maxPacketSize;

    private PlayerContext context;
    private Buffer receiveBuffer = Buffer.buffer();

    public TcpPeer(
            NetSocket socket,
            PacketDispatcher packetDispatcher,
            PlayerRegistry playerRegistry,
            int maxPacketSize
    ) {
        this.socket = socket;
        this.packetDispatcher = packetDispatcher;
        this.playerRegistry = playerRegistry;
        this.maxPacketSize = maxPacketSize;
    }

    public void start() {
        context = playerRegistry.register(socket);

        socket.handler(this::onChunk)
                .closeHandler(v -> onClose())
                .exceptionHandler(this::onException);
    }

    private void onChunk(Buffer chunk) {
        if (receiveBuffer.length() + chunk.length() > maxPacketSize) {
            socket.close();
            return;
        }

        receiveBuffer.appendBuffer(chunk);

        try {
            while (true) {
                var declaredPacketSize = VarInt.read(receiveBuffer);
                var receivedPacketSize = receiveBuffer.length() - declaredPacketSize.getLength();

                if (receivedPacketSize >= declaredPacketSize.getValue()) {
                    var packetId = receiveBuffer.getShort(declaredPacketSize.getLength());
                    var payload = Buffer.buffer(receiveBuffer.getByteBuf().slice(
                            declaredPacketSize.getLength() + PacketSender.PACKET_ID_LENGTH,
                            declaredPacketSize.getValue() - PacketSender.PACKET_ID_LENGTH
                    ));

                    packetDispatcher.dispatch(context, packetId, payload);

                    receiveBuffer = receiveBuffer.getBuffer(
                            declaredPacketSize.getLength() + declaredPacketSize.getValue(),
                            receiveBuffer.length()
                    );
                } else {
                    break;
                }
            }
        } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
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
