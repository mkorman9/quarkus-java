package com.github.mkorman9.game.dto.packet.login;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.mkorman9.game.dto.packet.Response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginFailedResponsePacket implements Response {
    public static int ID = 0x01;

    private String message;

    @Override
    @JsonIgnore
    public int getPacketId() {
        return ID;
    }
}
