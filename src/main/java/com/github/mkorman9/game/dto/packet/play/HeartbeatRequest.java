package com.github.mkorman9.game.dto.packet.play;

import com.github.mkorman9.game.dto.packet.Sendable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HeartbeatRequest implements Sendable {
    public static int ID = 0x00;

    private long data;

    @Override
    public int packetId() {
        return ID;
    }
}
