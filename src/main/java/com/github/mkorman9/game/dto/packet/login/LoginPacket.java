package com.github.mkorman9.game.dto.packet.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginPacket {
    public static short ID = 0x00;

    private String token;
}
