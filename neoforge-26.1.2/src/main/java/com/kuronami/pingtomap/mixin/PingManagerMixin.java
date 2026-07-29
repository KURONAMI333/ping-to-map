package com.kuronami.pingtomap.mixin;

import nx.pingwheel.common.core.PingManager;
import nx.pingwheel.common.network.PingLocationS2CPacket;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Ping-Wheel の `PingManager.acceptPingPacket` フック。
 *
 * Ping-Wheel は公式 API を提供していないため、本 MOD では Mixin で
 * メソッドの先頭に @Inject して、受信した ping packet を傍受する。
 *
 * 取得した PingLocationS2CPacket には:
 *  - pos: Vec3 (ping された世界座標)
 *  - author: UUID (ping を打ったプレイヤー)
 *  - channel: String (Ping-Wheel のチャンネル)
 * が含まれる。これを JourneyMap API へ転送する。
 *
 * 注: Mixin は @Inject で「割り込む」だけで Ping-Wheel 本来の処理は止めない (ci.cancel しない)。
 */
@Mixin(PingManager.class)
public abstract class PingManagerMixin {

    @Inject(
            method = "acceptPingPacket",
            at = @At("HEAD")
    )
    private static void pingtomap$onPingReceived(PingLocationS2CPacket packet, CallbackInfo ci) {
        // JM 連携は別 class に委譲 (Mixin class は静的フックに専念)
        com.kuronami.pingtomap.compat.jm.JourneyMapClientHook.onPingReceived(packet);
    }
}
