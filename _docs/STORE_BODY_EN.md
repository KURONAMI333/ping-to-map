# Ping to Map (P2M)

> Pop a temporary JourneyMap waypoint when someone pings a spot with Ping-Wheel.

You ping "come here!" with Ping-Wheel, but it never shows on the **map** — so for big builds people still can't find the spot. This addon drops a temporary JourneyMap waypoint the instant a ping happens, and clears it after ~30 seconds so the map stays tidy.

- 📍 **Instant temporary JM waypoint** on every ping (cyan, or the pinger's team colour)
- 🤝 **Made for co-op** — "rally point", "enemy spotted", "mine here" become visible at a glance
- 🕒 **Auto-expires in 30 s** (configurable 1–600 s, or fully persistent)
- 🌐 **Client-side only** — no need to install it on the server
- 💡 **Pure addon** — depends on Ping-Wheel + JourneyMap you already have; no items/blocks

## How it works

Ping-Wheel has no public API, so P2M uses a Mixin (`@Inject(at = @At("HEAD"))`) on Ping-Wheel's ping handler to read the pinged coordinates + author, and forwards them to JourneyMap's waypoint API. Ping-Wheel's own behaviour is never interrupted. Inner-class isolation means it never crashes if JourneyMap is absent.

## Supported loaders / versions (v1.0.0+)

| Minecraft | NeoForge | Forge | Fabric |
|---|:---:|:---:|:---:|
| 1.21.1 | ✅ | ✅ | ⚠️ chat-only |
| 1.20.1 | — | ✅ | ⚠️ chat-only |

- ✅ = full JourneyMap integration · ⚠️ chat-only = JM integration disabled, ping received only · — = NeoForge has no 1.20.1

## Configuration

`config/pingtomap-client.toml` (or the Mod Config GUI on NeoForge/Forge):

| Key | Default | Description |
|---|---|---|
| `feature.enabled` | true | Master switch |
| `feature.registerOwnPings` | true | Also waypoint your own pings (false = teammates' pings only) |
| `appearance.waypointLifetimeSec` | 30 | Seconds the waypoint stays (-1 = permanent) |
| `appearance.useTeamColor` | true | Use vanilla scoreboard team colour (false = fixed cyan) |

## Compatibility

| Mod | Support | Note |
|---|---|---|
| **Ping-Wheel** | required | Mixin target |
| **JourneyMap** | optional (CLIENT only) | Waypoint target; silently ignored if absent |
| Voice Chat mods (Plasmo, etc.) | unaffected | Ping-Wheel coexists with them, so does this |
| Xaero's Minimap / Worldmap | not supported | Xaero has no public API |

## Known limitations

**Fabric builds — JourneyMap integration disabled.** On the 1.20.1 / 1.21.1 Fabric builds, the Ping-Wheel Mixin hook works but the JM registration call is skipped (Loom 1.14, required by the JM Fabric jar, is unreleased). A reflection-bridge workaround is planned. **No NeoForge 1.20.1 build** — use Forge 1.20.1 for 1.20.1.

## Install

1. Install your loader for your MC version.
2. Install [Ping-Wheel](https://modrinth.com/mod/ping-wheel) (required).
3. **Forge / NeoForge:** install [JourneyMap](https://modrinth.com/mod/journeymap) (recommended). **Fabric:** JM integration currently disabled (chat-only).
4. **Fabric only:** also install [Forge Config API Port](https://modrinth.com/mod/forge-config-api-port).
5. Drop the `pingtomap-1.0.3.jar` for your loader/MC (latest) from the releases page into `mods/` (**client only** — no server install needed).

## License

MIT — modpack use, modification and redistribution OK, credit not required (welcome).

Author: KURONAMI · Built on Ping-Wheel / JourneyMap. Sister mod: Compass to Map.
