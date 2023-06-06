package com.github.mkorman9.game.server;

import com.github.mkorman9.game.dto.PlayerContext;
import com.github.mkorman9.game.dto.PlayerDisconnectReason;
import com.github.mkorman9.game.dto.VarInt;
import com.github.mkorman9.game.service.PacketDispatcher;
import com.github.mkorman9.game.service.PlayerRegistry;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketException;

/**
 * TcpPeer is instantiated for every TCP peer connection accepted by the server.
 * It is responsible for handling underlying socket operations and de-segmentation of received network chunks.
 * The structure of a single chunk is the following:
 * <pre>
 * {@code
 * Chunk:
 *   -----------------------------------------------
 *   | Packet Length | Packet:                     |
 *   |   (VarInt)    | --------------------------- |
 *   |               | |  Packet ID |   Payload  | |
 *   |               | |  (VarInt)  |   (bytes)  | |
 *   |               | --------------------------- |
 *   -----------------------------------------------
 *   }
 * </pre>
 * Packet Length and Packet ID are represented by Variable Length Quantity (see {@link VarInt}).
 * Payload content is dependent on the value of Packet ID.
*/
@Slf4j
public class TcpPeer {
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
                .exceptionHandler(this::onException)
                .resume();
    }

    private void onChunk(Buffer chunk) {
        if (receiveBuffer.length() + chunk.length() > maxPacketSize) {
            context.disconnect(PlayerDisconnectReason.POLICY_VIOLATION);
            return;
        }

        receiveBuffer.appendBuffer(chunk);

        try {
            while (true) {
                var declaredPacketSize = VarInt.read(receiveBuffer);
                var receivedPacketSize = receiveBuffer.length() - declaredPacketSize.getLength();

                if (declaredPacketSize.getValue() > 0 && receivedPacketSize >= declaredPacketSize.getValue()) {
                    var packetId = VarInt.read(receiveBuffer, declaredPacketSize.getLength());
                    var payload = Buffer.buffer(receiveBuffer.getByteBuf().slice(
                            declaredPacketSize.getLength() + packetId.getLength(),
                            declaredPacketSize.getValue() - packetId.getLength()
                    ));

                    packetDispatcher.dispatch(context, packetId.getValue(), payload);

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

        log.error("Error in TCP connection handler", t);
    }
}
