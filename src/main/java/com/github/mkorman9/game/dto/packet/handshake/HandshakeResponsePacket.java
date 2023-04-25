package com.github.mkorman9.game.dto.packet.handshake;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.mkorman9.game.dto.packet.Response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HandshakeResponsePacket implements Response {
    public static int ID = 0x00;

    private int players;

    private String serverVersion;

    @Override
    @JsonIgnore
    public int getPacketId() {
        return ID;
    }
}