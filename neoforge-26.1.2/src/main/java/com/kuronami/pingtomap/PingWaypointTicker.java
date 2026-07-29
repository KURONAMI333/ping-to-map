package com.kuronami.pingtomap;

import com.kuronami.pingtomap.compat.jm.JourneyMapClientHook;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

/**
 * Drives expiry of ping waypoints on a periodic client tick.
 *
 * <p>Without this, {@link JourneyMapClientHook#sweepExpired()} only ran when the
 * <em>next</em> ping arrived, so a lone ping's waypoint stayed on the map past its
 * lifetime until manually deleted (ping-to-map issue #1). Calling it every client
 * tick makes removal timer-driven: a waypoint disappears on schedule whether or not
 * another ping ever comes. Mirrors the Xaero variant's {@code PingWaypointTracker}.
 *
 * <p>Client tick runs on the render thread, which is where the JourneyMap client API
 * is meant to be touched. {@code sweepExpired()} early-returns when nothing is tracked,
 * so the per-tick cost is negligible.
 */
@EventBusSubscriber(modid = PingToMap.MODID, value = Dist.CLIENT)
public final class PingWaypointTicker {

    private PingWaypointTicker() {}

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
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
