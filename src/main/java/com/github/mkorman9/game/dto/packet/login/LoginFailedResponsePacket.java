package com.github.mkorman9.game.dto.packet.login;

import com.github.mkorman9.game.dto.packet.Response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginFailedResponsePacket implements Response {
    public static short ID = 0x01;

    private String message;

    @Override
    public short packetId() {
        return ID;
    }
}
