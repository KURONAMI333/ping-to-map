# Ping to Map

Drops a temporary JourneyMap waypoint the instant someone pings a spot with Ping-Wheel.

You ping "come here" with Ping-Wheel, but it never shows on the map, so on big builds people still can't find the spot. This addon puts a waypoint there the moment a ping happens and clears it after about 30 seconds so the map stays tidy.

**Features**

- A temporary JourneyMap waypoint on every ping, in cyan or the pinger's team colour
- Auto-expires after 30 s (configurable 1–600 s, or fully persistent)
- Made for co-op — rally points, "enemy spotted", "mine here" become visible at a glance
- No items or blocks; it never interrupts Ping-Wheel's own behaviour, and won't crash if JourneyMap is absent

**Config** (`config/pingtomap-client.toml`, or the Mod Config GUI)

- `feature.registerOwnPings` — also waypoint your own pings (false = teammates' only)
- `appearance.waypointLifetimeSec` — seconds the waypoint stays (-1 = permanent)
- `appearance.useTeamColor` — use your scoreboard team colour (false = fixed cyan)

**Dependencies**

- [Ping-Wheel](https://modrinth.com/mod/ping-wheel) — required
- [JourneyMap](https://modrinth.com/mod/journeymap) (client) — the waypoint target
- Fabric only: [Forge Config API Port](https://modrinth.com/mod/forge-config-api-port)

On the Fabric builds, JourneyMap registration is currently disabled (the JM Fabric jar needs an unreleased Loom version); the ping hook still fires.

Client-side only — no server install needed. Sister mod: Compass to Map.

Free to use in any modpack. Source and issues: https://github.com/KURONAMI333/ping-to-map
