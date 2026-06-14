package com.kuronami.pingtomap;

import com.kuronami.pingtomap.compat.jm.JourneyMapClientHook;
import com.mojang.logging.LogUtils;

import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

/**
 * Ping to Map: Fabric 1.21.11 entry (CLIENT only)。
 *
 * Mixin 経由で Ping-Wheel の {@code PingManager.acceptPingPacket} をフックし、
 * 受信した ping を JourneyMap に一時 waypoint として登録する。
 */
public class PingToMapFabric implements ClientModInitializer {

    public static final String MODID = "pingtomap";
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitializeClient() {
        // Config 登録 (FCAP v5 経由)
        ConfigRegistry.INSTANCE.register(MODID, ModConfig.Type.CLIENT, Config.SPEC);

        // 期限切れ waypoint を毎 client tick で sweep (lone ping も時間で確実に消す)
        ClientTickEvents.END_CLIENT_TICK.register(mc -> JourneyMapClientHook.sweepExpired());

        // ワールド退出時に追跡中の ping waypoint を全削除 (リーク防止)
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> JourneyMapClientHook.clearAll());

        LOGGER.info("Ping to Map (Fabric 1.21.11) initialized");
    }
}
