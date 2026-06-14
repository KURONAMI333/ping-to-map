package com.kuronami.pingtomap;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Ping to Map CLIENT 設定 (Forge 1.21.1)。
 */
public final class Config {
    private static final ForgeConfigSpec.Builder B = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue ENABLED = B
            .comment("Master switch. If false, ping waypoints are not registered.")
            .define("feature.enabled", true);

    public static final ForgeConfigSpec.BooleanValue REGISTER_OWN_PINGS = B
            .comment("If true, your own pings are also registered as waypoints. If false, only teammate pings.")
            .define("feature.registerOwnPings", true);

    public static final ForgeConfigSpec.BooleanValue SYNC_WITH_PING_WHEEL = B
            .comment(
                    "If true (default), the map waypoint disappears at the same time as the Ping-Wheel ping:",
                    "its lifetime follows Ping-Wheel's own pingDuration setting, so the in-world ping and the",
                    "map waypoint vanish together. If false, the fixed 'waypointLifetimeSec' below is used."
            )
            .define("appearance.syncWithPingWheel", true);

    public static final ForgeConfigSpec.IntValue WAYPOINT_LIFETIME_SEC = B
            .comment(
                    "Manual waypoint lifetime in seconds. Only used when 'syncWithPingWheel' is false.",
                    "Set to -1 for permanent waypoints (not recommended for ping use case)."
            )
            .defineInRange("appearance.waypointLifetimeSec", 30, -1, 600);

    public static final ForgeConfigSpec.BooleanValue USE_TEAM_COLOR = B
            .comment(
                    "If true, use the pinger's team color (vanilla scoreboard team).",
                    "If false, all ping waypoints use the brand color (cyan #00FFFF)."
            )
            .define("appearance.useTeamColor", true);

    static final ForgeConfigSpec SPEC = B.build();

    private Config() {}
}
