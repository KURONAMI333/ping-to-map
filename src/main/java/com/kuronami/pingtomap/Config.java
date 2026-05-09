package com.kuronami.pingtomap;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Ping to Map の CLIENT 設定 (Ping-Wheel の処理は完全クライアントサイドのため)。
 */
public final class Config {
    private static final ModConfigSpec.Builder B = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLED = B
            .comment("Master switch. If false, ping waypoints are not registered.")
            .define("feature.enabled", true);

    public static final ModConfigSpec.BooleanValue REGISTER_OWN_PINGS = B
            .comment("If true, your own pings are also registered as waypoints. If false, only teammate pings.")
            .define("feature.registerOwnPings", true);

    public static final ModConfigSpec.IntValue WAYPOINT_LIFETIME_SEC = B
            .comment(
                    "How long the ping waypoint stays on the map (seconds).",
                    "Set to -1 for permanent waypoints (not recommended for ping use case)."
            )
            .defineInRange("appearance.waypointLifetimeSec", 30, -1, 600);

    public static final ModConfigSpec.BooleanValue USE_TEAM_COLOR = B
            .comment(
                    "If true, use the pinger's team color (vanilla scoreboard team).",
                    "If false, all ping waypoints use the brand color (cyan #00FFFF)."
            )
            .define("appearance.useTeamColor", true);

    static final ModConfigSpec SPEC = B.build();

    private Config() {}
}
