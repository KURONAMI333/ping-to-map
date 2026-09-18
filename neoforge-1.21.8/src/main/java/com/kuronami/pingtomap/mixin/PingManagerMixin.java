package com.kuronami.pingtomap.mixin;

import net.minecraft.client.Minecraft;

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
 * <p><b>Threading</b>: Ping-Wheel 本来の {@code acceptPingPacket} は netty I/O thread で
 * 実行される。{@link com.kuronami.pingtomap.compat.jm.JourneyMapClientHook#onPingReceived}
 * は {@code Minecraft.getInstance()} 等 main thread 専用 API に触れるため、
 * {@code Minecraft.getInstance().execute(...)} で main thread にマーシャルしてから呼ぶ。
 * {@code packet} は record (immutable) なので netty thread でそのまま capture して
 * main thread に渡してよい。
 *
 * 注: Mixin は @Inject で「割り込む」だけで Ping-Wheel 本来の処理は止めない (ci.cancel しない)。
 * すべての例外を握りつぶし、Ping-Wheel の通常動作を阻害しない。
 */
@Mixin(PingManager.class)
public abstract class PingManagerMixin {

    @Inject(
            method = "acceptPingPacket",
            at = @At("HEAD"),
            require = 0
    )
    private static void pingtomap$onPingReceived(PingLocationS2CPacket packet, CallbackInfo ci) {
        try {
            // JM 連携は別 class に委譲 (Mixin class は静的フックに専念)。
            // netty I/O thread から呼ばれるため、main thread にマーシャルしてから処理する。
            Minecraft.getInstance().execute(() -> {
                try {
                    com.kuronami.pingtomap.compat.jm.JourneyMapClientHook.onPingReceived(packet);
                } catch (Throwable ignored) {
                    // JM 連携で何が起きても Ping-Wheel 通常動作を阻害しない。
                }
            });
        } catch (Throwable t) {
            // Mixin 自体が落ちないように二重ガード。
        }
    }
}
