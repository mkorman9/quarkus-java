package com.github.mkorman9.game.dto.packet.login;

import com.github.mkorman9.game.dto.packet.Sendable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginSuccessResponsePacket implements Sendable {
    public static int ID = 0x00;

    private Instant timestamp;

    private Set<String> roles;

    @Override
    public int packetId() {
        return ID;
    }
}
