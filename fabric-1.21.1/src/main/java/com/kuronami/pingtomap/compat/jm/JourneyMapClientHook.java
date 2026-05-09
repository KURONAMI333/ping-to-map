package com.kuronami.pingtomap.compat.jm;

import com.kuronami.pingtomap.PingToMapFabric;

import nx.pingwheel.common.network.PingLocationS2CPacket;

/**
 * v1.0 Fabric: JM 統合 disable。Mixin で受信した ping packet を log するのみ。
 * v1.1 で reflection ベースの JM hook を追加予定。
 */
public final class JourneyMapClientHook {

    private JourneyMapClientHook() {}

    public static void onPingReceived(PingLocationS2CPacket packet) {
        // No-op on Fabric v1.0. Server-side chat 通知 (Ping-Wheel 自身) が代役。
        if (packet != null) {
            PingToMapFabric.LOGGER.debug(
                    "Ping received but JM integration disabled on Fabric v1.0: author={}, pos={}",
                    packet.author(), packet.pos());
        }
    }
}
