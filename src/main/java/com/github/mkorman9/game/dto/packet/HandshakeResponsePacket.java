package com.github.mkorman9.game.dto.packet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HandshakeResponsePacket {
    public static int ID = 0x00;

    private int players;

    private String serverVersion;
}
