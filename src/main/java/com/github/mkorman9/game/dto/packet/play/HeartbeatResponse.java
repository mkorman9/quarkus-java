package com.github.mkorman9.game.dto.packet.play;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HeartbeatResponse {
    public static int ID = 0x00;

    private long data;
}
