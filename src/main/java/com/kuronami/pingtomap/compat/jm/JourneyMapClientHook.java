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
import net.neoforged.fml.ModList;
import nx.pingwheel.common.network.PingLocationS2CPacket;

/**
 * Ping-Wheel の S2C packet を受け取って JourneyMap に**一時 waypoint** を登録する。
 *
 * 設計:
 *  - JM 不在時に NoClassDefFoundError を起こさないため Inner class で API 参照を分離
 *  - 一時 waypoint: 各 waypoint は登録時に「いつ削除するか」を記録、
 *    後続の ping 受信時 (= 次の tick 相当のタイミング) に期限切れをチェックして削除
 *  - 同一プレイヤーが連続 ping した時は古い waypoint を上書き (UUID + sequence で識別)
 */
public final class JourneyMapClientHook {

    /** ブランド色 (シアン、ping らしさ) */
    private static final int BRAND_COLOR = 0x00FFFF;

    /** 登録した一時 waypoint の追跡 (UUID(author) → expireAtMillis + waypointGuid) */
    private static final Map<UUID, ScheduledRemoval> TRACKED = Collections.synchronizedMap(new LinkedHashMap<>());

    private record ScheduledRemoval(String waypointGuid, long expireAtMillis) {}

    private JourneyMapClientHook() {}

    public static boolean isJourneyMapLoaded() {
        return ModList.get() != null && ModList.get().isLoaded("journeymap");
    }

    /**
     * Mixin から呼ばれるエントリーポイント。
     * Ping-Wheel の packet を受信した瞬間にこの method が走る (CLIENT thread)。
     */
    public static void onPingReceived(PingLocationS2CPacket packet) {
        if (!Config.ENABLED.get()) return;
        if (packet == null) return;
        if (!isJourneyMapLoaded()) return;

        try {
            // 自分の ping は登録するか?
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
     * 期限切れの waypoint を削除する (次の ping 受信時 or 定期 tick で呼ばれる想定)。
     * 現状は ping 受信時に呼ばれる: ping 頻度より expire 時間のほうが長いと残るが、
     * Persistent=false で JM 自体が終了時に消すため実害は限定的。
     */
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

    /**
     * JM API への参照を Inner class に閉じ込める isolation パターン。
     */
    private static final class Inner {
        static void show(PingLocationS2CPacket packet) {
            journeymap.api.v2.client.IClientAPI api = PingToMapJourneyMapPlugin.api;
            if (api == null) {
                PingToMap.LOGGER.debug("JourneyMap API not yet initialized, skipping ping waypoint");
                return;
            }
            // 既存の同一 author の waypoint を先に sweep (連続 ping で waypoint が乱立しないよう)
            sweepExpired();
            removePrevious(packet.author());

            Vec3 pos = packet.pos();
            BlockPos bpos = new BlockPos(
                    (int) Math.floor(pos.x),
                    (int) Math.floor(pos.y),
                    (int) Math.floor(pos.z)
            );

            // dimension: client の現在 dimension を使う (ping は同一 dim 内でのみ伝達される前提)
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

            // 一時 waypoint: persistent=false で JM の世代越え保存をしない
            journeymap.api.v2.common.waypoint.Waypoint wp =
                    journeymap.api.v2.common.waypoint.WaypointFactory.createClientWaypoint(
                            PingToMap.MODID,
                            bpos,
                            displayName,
                            dim,
                            /*persistent=*/ false
                    );
            wp.setColor(color);
            api.addWaypoint(PingToMap.MODID, wp);

            // 期限を記録
            int lifetimeSec = Config.WAYPOINT_LIFETIME_SEC.get();
            if (lifetimeSec > 0) {
                long expireAt = System.currentTimeMillis() + lifetimeSec * 1000L;
                TRACKED.put(packet.author(), new ScheduledRemoval(wp.getGuid(), expireAt));
            }

            PingToMap.LOGGER.info("Ping waypoint registered: {} @ {} (color=0x{}, lifetime={}s)",
                    displayName, bpos, Integer.toHexString(color), lifetimeSec);
        }

        static void remove(String waypointGuid) {
            journeymap.api.v2.client.IClientAPI api = PingToMapJourneyMapPlugin.api;
            if (api == null) return;
            // JM API には GUID 直接削除がないので、modid + GUID で waypoint を取得して remove
            journeymap.api.v2.common.waypoint.Waypoint wp = api.getWaypoint(PingToMap.MODID, waypointGuid);
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
            // 自分?
            UUID self = Minecraft.getInstance().player != null
                    ? Minecraft.getInstance().player.getUUID() : null;
            if (self != null && self.equals(authorId)) {
                return Minecraft.getInstance().player.getGameProfile().getName();
            }
            return "Player";
        }

        /**
         * vanilla scoreboard team の色を取得。team 未所属なら null。
         */
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
