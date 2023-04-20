package com.github.mkorman9.game.dto.packet.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginFailedResponsePacket {
    public static int ID = 0x01;

    private String message;
}
