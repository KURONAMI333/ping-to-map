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
 * 1.21+ の v2 API (WaypointFactory) と異なり、JM 5.10.5 の v1 API を使う。
 *
 * 寿命は既定で Ping-Wheel の pingDuration に同期 ({@code resolveLifetimeSec})、毎 client tick
 * ({@code PingWaypointTicker}) で期限切れを掃除 → ワールド内の ping と waypoint が同時に消える。
 */
public final class JourneyMapClientHook {

    private static final int BRAND_COLOR = 0x00FFFF;
    private static final Map<UUID, ScheduledRemoval> TRACKED = Collections.synchronizedMap(new LinkedHashMap<>());

    private record ScheduledRemoval(String waypointId, long expireAtNanos) {}

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

    /**
     * 期限切れ waypoint を削除。{@code PingWaypointTicker} が毎 client tick で呼ぶので、
     * 後続 ping が無くても lifetime 経過時に確実に消える。
     */
    public static void sweepExpired() {
        if (TRACKED.isEmpty()) return;
        if (!isJourneyMapLoaded()) return;
        long now = System.nanoTime();
        synchronized (TRACKED) {
            Iterator<Map.Entry<UUID, ScheduledRemoval>> it = TRACKED.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<UUID, ScheduledRemoval> e = it.next();
                if (now - e.getValue().expireAtNanos >= 0) {
                    try {
                        Inner.remove(e.getValue().waypointId);
                    } catch (Throwable t) {
                        PingToMap.LOGGER.debug("ping waypoint removal failed: {}", t.toString());
                    }
                    it.remove();
                }
            }
        }
    }

    /**
     * 追跡中の ping waypoint をすべて削除する。logout / level unload 時に
     * {@code PingWaypointTicker} から呼ばれ、ワールドをまたいだ追跡リークを防ぐ。
     */
    public static void clearAll() {
        if (TRACKED.isEmpty()) return;
        synchronized (TRACKED) {
            for (ScheduledRemoval r : TRACKED.values()) {
                try {
                    Inner.remove(r.waypointId);
                } catch (Throwable t) {
                    PingToMap.LOGGER.debug("ping waypoint clearAll removal failed: {}", t.toString());
                }
            }
            TRACKED.clear();
        }
    }

    /**
     * waypoint の寿命 (秒) を決める。{@code syncWithPingWheel} が ON (既定) なら
     * Ping-Wheel の {@code pingDuration} に追従し、ワールド内の ping と waypoint が
     * 同時に消える。Ping-Wheel は pingDuration が 60 以上だと ping を永続扱いにする
     * ({@code PingView.isExpired} が {@code pingDuration < 60} を条件にしている) ので、
     * その場合は -1 (永続) を返して同期を保つ。同期 OFF か config 読み取り失敗時は
     * 手動の {@code waypointLifetimeSec} にフォールバック。
     */
    private static int resolveLifetimeSec() {
        if (Config.SYNC_WITH_PING_WHEEL.get()) {
            try {
                int pingDuration = nx.pingwheel.common.config.ClientConfig.HANDLER.getConfig().getPingDuration();
                return pingDuration >= 60 ? -1 : pingDuration;
            } catch (Throwable t) {
                PingToMap.LOGGER.debug("Ping-Wheel pingDuration を読めず手動寿命にフォールバック: {}", t.toString());
            }
        }
        return Config.WAYPOINT_LIFETIME_SEC.get();
    }

    private static final class Inner {
        static void show(PingLocationS2CPacket packet) throws Exception {
            if (Minecraft.getInstance().level == null) return;
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
                    Minecraft.getInstance().level.dimension();

            String authorName = resolveAuthorName(packet.author());
            String displayName = "📍 " + authorName + "'s Ping";

            int color = BRAND_COLOR;
            if (Config.USE_TEAM_COLOR.get()) {
                Integer teamColor = resolveTeamColor(packet.author());
                if (teamColor != null) color = teamColor;
            }

            journeymap.client.api.display.Waypoint wp =
                    new journeymap.client.api.display.Waypoint(PingToMap.MODID, displayName, dim, bpos);
            wp.setColor(color);
            wp.setPersistent(false);
            api.show(wp);

            // 寿命は Ping-Wheel のピン表示時間に同期するのが既定 → 同時に消える
            int lifetimeSec = resolveLifetimeSec();
            // 0秒は次の掃除で削除し、-1だけを永続として追跡しない。
            if (lifetimeSec >= 0) {
                long expireAt = System.nanoTime() + lifetimeSec * 1_000_000_000L;
                TRACKED.put(packet.author(), new ScheduledRemoval(wp.getId(), expireAt));
            }

            PingToMap.LOGGER.info("Ping waypoint registered: {} @ {} (color=0x{}, lifetime={}s)",
                    displayName, bpos, Integer.toHexString(color), lifetimeSec);
        }

        static void remove(String waypointId) {
            journeymap.client.api.IClientAPI api = PingToMapJourneyMapPlugin.api;
            if (api == null) return;
            journeymap.client.api.display.Waypoint wp = api.getWaypoint(PingToMap.MODID, waypointId);
            if (wp != null) {
                api.remove(wp);
            }
        }

        private static void removePrevious(UUID author) {
            ScheduledRemoval prev = TRACKED.remove(author);
            if (prev == null) return;
            try {
                remove(prev.waypointId);
            } catch (Throwable t) {
                PingToMap.LOGGER.debug("previous ping waypoint removal failed: {}", t.toString());
            }
        }

        private static String resolveAuthorName(UUID authorId) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.player.getUUID().equals(authorId)) {
                return mc.player.getGameProfile().getName();
            }
            if (mc.getConnection() != null) {
                net.minecraft.client.multiplayer.PlayerInfo info = mc.getConnection().getPlayerInfo(authorId);
                if (info != null) return info.getProfile().getName();
            }
            if (mc.level != null && mc.level.getPlayerByUUID(authorId) instanceof AbstractClientPlayer p) {
                return p.getGameProfile().getName();
            }
            return "Player";
        }

        /** Tab 一覧なら追跡範囲外の仲間からもチーム色を取得できる。 */
        private static Integer resolveTeamColor(UUID authorId) {
            Minecraft mc = Minecraft.getInstance();
            net.minecraft.world.scores.PlayerTeam team = null;
            if (mc.getConnection() != null) {
                net.minecraft.client.multiplayer.PlayerInfo info = mc.getConnection().getPlayerInfo(authorId);
                if (info != null) team = info.getTeam();
            }
            if (team == null && mc.level != null) {
                net.minecraft.world.entity.player.Player p = mc.level.getPlayerByUUID(authorId);
                if (p != null) team = (net.minecraft.world.scores.PlayerTeam) p.getTeam();
            }
            if (team == null) return null;
            net.minecraft.ChatFormatting fmt = team.getColor();
            return fmt != null ? fmt.getColor() : null;
        }
    }
}
