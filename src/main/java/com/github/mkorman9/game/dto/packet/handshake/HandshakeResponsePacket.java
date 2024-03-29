package com.github.mkorman9.game.dto.packet.handshake;

import com.github.mkorman9.game.dto.packet.Sendable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HandshakeResponsePacket implements Sendable {
    public static int ID = 0x00;

    private int players;

    private String serverVersion;

    @Override
    public int packetId() {
        return ID;
    }
}
