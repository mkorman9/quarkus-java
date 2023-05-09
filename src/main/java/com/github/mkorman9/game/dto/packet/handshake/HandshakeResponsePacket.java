package com.github.mkorman9.game.dto.packet.handshake;

import com.github.mkorman9.game.dto.packet.Response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HandshakeResponsePacket implements Response {
    public static short ID = 0x00;

    private int players;

    private String serverVersion;

    @Override
    public short packetId() {
        return ID;
    }
}
