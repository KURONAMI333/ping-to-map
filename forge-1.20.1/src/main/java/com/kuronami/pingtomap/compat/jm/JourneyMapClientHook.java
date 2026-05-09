package com.kuronami.pingtomap.compat.jm;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import com.kuronami.pingtomap.Config;
import com.kuronami.pingtomap.PingToMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;
import nx.pingwheel.common.network.PingLocationS2CPacket;

/**
 * Ping-Wheel S2C packet を JM 一時 waypoint に登録 (Forge 1.20.1, JM v1 API)。
 * 1.21+ の v2 API (WaypointFactory) との差分: Waypoint.Builder 経由。
 */
public final class JourneyMapClientHook {

    private static final int BRAND_COLOR = 0x00FFFF;
    private static final Map<UUID, ScheduledRemoval> TRACKED = Collections.synchronizedMap(new LinkedHashMap<>());

    private record ScheduledRemoval(String waypointGuid, long expireAtMillis) {}

    private JourneyMapClientHook() {}

    public static boolean isJourneyMapLoaded() {
        return ModList.get() != null && ModList.get().isLoaded("journeymap");
    }

    public static void onPingReceived(PingLocationS2CPacket packet) {
        if (!Config.ENABLED.get()) return;
        if (packet == null) return;
        if (!isJourneyMapLoaded()) return;

        try {
            UUID author = packet.author();
            UUID self = Minecraft.getInstance().player != null
                    ? Minecraft.getInstance().player.getUUID() : null;
            if (!Config.REGISTER_OWN_PINGS.get() && self != null && self.equals(author)) {
                return;
            }
            Inner.show(packet);
        } catch (Throwable t) {
            PingToMap.LOGGER.warn("JourneyMap ping waypoint show failed: {}", t.toString());
        }
    }

    public static void sweepExpired() {
        if (!isJourneyMapLoaded()) return;
        long now = System.currentTimeMillis();
        synchronized (TRACKED) {
            Iterator<Map.Entry<UUID, ScheduledRemoval>> it = TRACKED.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<UUID, ScheduledRemoval> e = it.next();
                if (e.getValue().expireAtMillis <= now) {
                    try {
                        Inner.remove(e.getValue().waypointGuid);
                    } catch (Throwable t) {
                        PingToMap.LOGGER.debug("ping waypoint removal failed: {}", t.toString());
                    }
                    it.remove();
                }
            }
        }
    }

    private static final class Inner {
        static void show(PingLocationS2CPacket packet) {
            journeymap.client.api.IClientAPI api = PingToMapJourneyMapPlugin.api;
            if (api == null) {
                PingToMap.LOGGER.debug("JourneyMap API not yet initialized, skipping ping waypoint");
                return;
            }
            sweepExpired();
            removePrevious(packet.author());

            Vec3 pos = packet.pos();
            BlockPos bpos = new BlockPos(
                    (int) Math.floor(pos.x),
                    (int) Math.floor(pos.y),
                    (int) Math.floor(pos.z)
            );

            net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dim =
                    Minecraft.getInstance().level != null
                            ? Minecraft.getInstance().level.dimension()
                            : net.minecraft.world.level.Level.OVERWORLD;

            String authorName = resolveAuthorName(packet.author());
            String displayName = "📍 " + authorName + "'s Ping";

            int color = BRAND_COLOR;
            if (Config.USE_TEAM_COLOR.get()) {
                Integer teamColor = resolveTeamColor(packet.author());
                if (teamColor != null) color = teamColor;
            }

            // JM 1.20.1 v1 API: Waypoint.Builder pattern
            journeymap.common.api.waypoint.Waypoint wp =
                    new journeymap.common.api.waypoint.Waypoint.Builder(PingToMap.MODID)
                            .withName(displayName)
                            .withBlockPos(bpos)
                            .withDimension(dim)
                            .withColorInt(color)
                            .isPersistent(false)
                            .build();
            api.addWaypoint(PingToMap.MODID, wp);

            int lifetimeSec = Config.WAYPOINT_LIFETIME_SEC.get();
            if (lifetimeSec > 0) {
                long expireAt = System.currentTimeMillis() + lifetimeSec * 1000L;
                TRACKED.put(packet.author(), new ScheduledRemoval(wp.getGuid(), expireAt));
            }

            PingToMap.LOGGER.info("Ping waypoint registered: {} @ {} (color=0x{}, lifetime={}s)",
                    displayName, bpos, Integer.toHexString(color), lifetimeSec);
        }

        static void remove(String waypointGuid) {
            journeymap.client.api.IClientAPI api = PingToMapJourneyMapPlugin.api;
            if (api == null) return;
            journeymap.common.api.waypoint.Waypoint wp = api.getWaypoint(PingToMap.MODID, waypointGuid);
            if (wp != null) {
                api.removeWaypoint(PingToMap.MODID, wp);
            }
        }

        private static void removePrevious(UUID author) {
            ScheduledRemoval prev = TRACKED.remove(author);
            if (prev == null) return;
            try {
                remove(prev.waypointGuid);
            } catch (Throwable t) {
                PingToMap.LOGGER.debug("previous ping waypoint removal failed: {}", t.toString());
            }
        }

        private static String resolveAuthorName(UUID authorId) {
            if (Minecraft.getInstance().level == null) return "Player";
            AbstractClientPlayer p = Minecraft.getInstance().level.getPlayerByUUID(authorId)
                    instanceof AbstractClientPlayer ap ? ap : null;
            if (p != null) return p.getGameProfile().getName();
            UUID self = Minecraft.getInstance().player != null
                    ? Minecraft.getInstance().player.getUUID() : null;
            if (self != null && self.equals(authorId)) {
                return Minecraft.getInstance().player.getGameProfile().getName();
            }
            return "Player";
        }

        private static Integer resolveTeamColor(UUID authorId) {
            if (Minecraft.getInstance().level == null) return null;
            net.minecraft.world.entity.player.Player p = Minecraft.getInstance().level.getPlayerByUUID(authorId);
            if (p == null) return null;
            net.minecraft.world.scores.PlayerTeam team = (net.minecraft.world.scores.PlayerTeam) p.getTeam();
            if (team == null) return null;
            net.minecraft.ChatFormatting fmt = team.getColor();
            if (fmt == null || fmt.getColor() == null) return null;
            return fmt.getColor();
        }
    }
}
