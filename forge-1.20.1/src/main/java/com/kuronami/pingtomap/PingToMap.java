package com.kuronami.pingtomap;

import com.mojang.logging.LogUtils;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import org.slf4j.Logger;

/**
 * Ping to Map: Ping-Wheel × JourneyMap Addon (Forge 1.21.1, CLIENT only).
 *
 * Forge 1.21.1 の @Mod は value のみ。CLIENT 限定は mods.toml の side="CLIENT" で指定。
 */
@Mod(PingToMap.MODID)
public class PingToMap {

    public static final String MODID = "pingtomap";
    public static final Logger LOGGER = LogUtils.getLogger();

    public PingToMap() {
        // CLIENT 専用 MOD
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
        // Mixin は src/main/resources/pingtomap.mixins.json で自動ロード
        LOGGER.info("Ping to Map (Forge 1.21.1) initialized");
    }
}
