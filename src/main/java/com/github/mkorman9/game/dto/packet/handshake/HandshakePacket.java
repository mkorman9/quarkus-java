package com.github.mkorman9.game.dto.packet.handshake;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HandshakePacket {
    public static int ID = 0x00;

    private String device;

    private String clientVersion;
}
