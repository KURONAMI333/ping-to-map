# Ping to Map

Drops a temporary JourneyMap waypoint the instant someone pings a spot with Ping-Wheel.

You ping "come here" with Ping-Wheel, but it never shows on the map, so on big builds people still can't find the spot. This addon puts a waypoint there the moment a ping happens, and by default it disappears together with the Ping-Wheel ping so the map stays tidy.

**Features**

- A temporary JourneyMap waypoint on every ping, in cyan or the pinger's team colour
- By default the waypoint expires in sync with the Ping-Wheel ping — they vanish together (or set a fixed 1–600 s, or make it permanent)
- Made for co-op — rally points, "enemy spotted", "mine here" become visible at a glance
- No items or blocks; it never interrupts Ping-Wheel's own behaviour, and won't crash if JourneyMap is absent

**Config** (`config/pingtomap-client.toml`, or the Mod Config GUI)

- `appearance.syncWithPingWheel` — waypoint vanishes together with the ping, its lifetime following the ping's (default on)
- `appearance.waypointLifetimeSec` — fixed lifetime in seconds, used only when sync is off (-1 = permanent)
- `appearance.useTeamColor` — use your scoreboard team colour (false = fixed cyan)
- `feature.registerOwnPings` — also waypoint your own pings (set false for teammates' pings only)

**Dependencies**

- [Ping-Wheel](https://modrinth.com/mod/ping-wheel) — required
- [JourneyMap](https://modrinth.com/mod/journeymap) (client) — the waypoint target
- Fabric only: [Forge Config API Port](https://modrinth.com/mod/forge-config-api-port)

Sister mod: Compass to Map.

All Rights Reserved. Modpack inclusion is allowed without permission or credit. Source: https://github.com/KURONAMI333/ping-to-map

## Published builds

The table lists files attached to the public GitHub release; choose the file for your Minecraft version and loader.

| Minecraft | NeoForge | Forge | Fabric |
|---|:---:|:---:|:---:|
| 1.20.1 | — | Yes | Yes |
| 1.21.1 | Yes | Yes | Yes |
| 1.21.4 | Yes | — | Yes |
| 1.21.11 | Yes | — | Yes |
| 26.1.2 | Yes | — | Yes |
| 26.2 | Yes | — | Yes |

## Downloads and support

Downloads: [CurseForge](https://www.curseforge.com/minecraft/mc-mods/ping-to-map) · [GitHub Releases](https://github.com/KURONAMI333/ping-to-map/releases/tag/v1.2.2).

For bugs and questions, comment on the [CurseForge page](https://www.curseforge.com/minecraft/mc-mods/ping-to-map) or DM [@kuronami333 on X](https://x.com/kuronami333).

[Source](https://github.com/KURONAMI333/ping-to-map) · [License](LICENSE)
