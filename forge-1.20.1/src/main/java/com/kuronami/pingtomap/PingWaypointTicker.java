package com.kuronami.pingtomap;

import com.kuronami.pingtomap.compat.jm.JourneyMapClientHook;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 毎 client tick で ping waypoint の期限切れを掃除する (Forge)。
 *
 * <p>これが無いと {@link JourneyMapClientHook#sweepExpired()} は次の ping 受信時にしか
 * 走らず、単発 ping の waypoint が lifetime を過ぎても残った。毎 tick 呼ぶことで、後続 ping が
 * 無くても waypoint が時刻通りに消える。logout / level unload では全 waypoint を削除する。
 */
@Mod.EventBusSubscriber(modid = PingToMap.MODID, value = Dist.CLIENT)
public final class PingWaypointTicker {

    private PingWaypointTicker() {}

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        JourneyMapClientHook.sweepExpired();
    }

    /** Logout = leaving the world on the client: drop all tracked ping waypoints. */
    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        JourneyMapClientHook.clearAll();
    }

    /** Safety net for single-player world switches. */
    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (Minecraft.getInstance() != null && event.getLevel() == Minecraft.getInstance().level) {
            JourneyMapClientHook.clearAll();
        }
    }
}
