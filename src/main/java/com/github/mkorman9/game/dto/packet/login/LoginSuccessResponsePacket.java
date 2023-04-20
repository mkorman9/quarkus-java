package com.github.mkorman9.game.dto.packet.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginSuccessResponsePacket {
    public static int ID = 0x00;

    private Instant timestamp;
}
