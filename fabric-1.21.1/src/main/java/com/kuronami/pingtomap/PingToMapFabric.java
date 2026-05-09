package com.kuronami.pingtomap;

import com.mojang.logging.LogUtils;

import fuzs.forgeconfigapiport.fabric.api.forge.v4.ForgeConfigRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

/**
 * Ping to Map: Fabric 1.21.1 entry (CLIENT only)。
 *
 * Mixin 経由で Ping-Wheel の {@code PingManager.acceptPingPacket} をフックする。
 * v1.0 Fabric では JM 連携は disable (Loom 1.14 制約)。
 */
public class PingToMapFabric implements ClientModInitializer {

    public static final String MODID = "pingtomap";
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitializeClient() {
        // Config 登録 (FCAP 経由)
        ForgeConfigRegistry.INSTANCE.register(MODID, ModConfig.Type.CLIENT, Config.SPEC);
        LOGGER.info("Ping to Map (Fabric 1.21.1) initialized - JM integration disabled in v1.0");
    }
}
