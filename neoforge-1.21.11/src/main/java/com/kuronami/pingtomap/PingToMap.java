package com.kuronami.pingtomap;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

import org.slf4j.Logger;

/**
 * Ping to Map: Ping-Wheel × JourneyMap Addon
 *
 * Ping-Wheel で誰かが ping した位置を、JourneyMap に**一時 waypoint** として
 * 自動登録する。チーム coop で「あそこ来て！」が地図上で一目でわかるようになる。
 *
 * 仕組み:
 *  - Mixin で Ping-Wheel の `PingManager.acceptPingPacket(PingLocationS2CPacket)` をフック
 *  - 受信した ping の座標 + author UUID を取得
 *  - JourneyMap API で一時 waypoint を登録 (デフォルト 30 秒で自動削除)
 *
 * 注: Ping-Wheel は公式 API を持たないため Mixin での実装。
 * Inner class isolation で JM 不在環境でも crash しない。
 */
@Mod(value = PingToMap.MODID, dist = net.neoforged.api.distmarker.Dist.CLIENT)
public class PingToMap {

    public static final String MODID = "pingtomap";
    public static final Logger LOGGER = LogUtils.getLogger();

    public PingToMap(IEventBus modEventBus, ModContainer modContainer) {
        // CLIENT 専用 MOD: サーバ側では何もしない
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.SPEC);

        // Config GUI 自動生成
        modContainer.registerExtensionPoint(
                net.neoforged.neoforge.client.gui.IConfigScreenFactory.class,
                net.neoforged.neoforge.client.gui.ConfigurationScreen::new
        );
    }
}
