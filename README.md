# Ping to Map (P2M)

> Pop a temporary JourneyMap waypoint when someone pings a location with Ping-Wheel.

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Modrinth](https://img.shields.io/badge/Modrinth-ping--to--map-00AF5C)](https://modrinth.com/mod/ping-to-map)
[![CurseForge](https://img.shields.io/badge/CurseForge-ping--to--map-F16436)](https://www.curseforge.com/minecraft/mc-mods/ping-to-map)

---

## Supported Loaders / Versions (v1.0.0+)

| Minecraft | NeoForge | Forge | Fabric |
|---|:---:|:---:|:---:|
| 1.21.1 | ✅ | ✅ | ⚠️ chat-only |
| 1.20.1 |  —  | ✅ | ⚠️ chat-only |

- ✅ = JourneyMap 統合フル対応（waypoint 自動登録）
- ⚠️ chat-only = JourneyMap 統合 disable、ping 受信のみ ([既知制限](#known-limitations) 参照)
- — = NeoForge は 1.20.1 リリースなし

---

## Why Ping to Map?

Ping-Wheel で「あそこ来て！」って ping を打っても、**地図上には載らない**から大きい施設だと結局見つけにくい。  
このアドオン MOD は **ping した瞬間に JM に一時 waypoint を立てる**。30 秒ほどで自動消滅するので地図が散らからない。

- 📍 **Ping した瞬間に JM 上に一時 waypoint** (シアン or チームカラー)
- 🤝 **チーム coop に最適** — 「集合場所」「敵発見」「採掘地点」を一目で共有
- 🕒 **30 秒で自動消滅** (Config で 1〜600 秒に変更可、永続化も可)
- 🌐 **クライアント MOD のみ** — サーバ側に入れる必要なし
- 💡 **既存 MOD に依存**: Ping-Wheel + JourneyMap が既に入ってるなら追加するだけ

---

## How it works (技術詳細)

Ping-Wheel は公式 API を持たないため、本 MOD は **Mixin** で `nx.pingwheel.common.core.PingManager#acceptPingPacket` をフック。受信した `PingLocationS2CPacket` から座標と author UUID を取得し、`IClientAPI.addWaypoint` に転送する。

- Mixin: `@Inject(at = @At("HEAD"))`、Ping-Wheel 本来の処理は止めない
- Inner class isolation: JM 不在環境でも crash しない
- 一時 waypoint: `persistent=false` + 自前の expire tracker で時間経過で削除

---

## Installation

1. **ローダーを導入** (Minecraft バージョンに合わせる):
   - 1.21.1 → [NeoForge](https://neoforged.net) / [Forge](https://files.minecraftforge.net) / [Fabric](https://fabricmc.net)
   - 1.20.1 → [Forge](https://files.minecraftforge.net) / [Fabric](https://fabricmc.net)
2. [Ping-Wheel](https://modrinth.com/mod/ping-wheel) を導入（必須）
3. **Forge / NeoForge のみ**: [JourneyMap](https://modrinth.com/mod/journeymap) を導入（推奨、これがないと waypoint 登録できない）  
   **Fabric**: 現状 JM 統合 disable ([既知制限](#known-limitations))
4. **Fabric のみ**: [Forge Config API Port](https://modrinth.com/mod/forge-config-api-port) を追加導入
5. リリースページから、自分のローダー × MC バージョン向けの `pingtomap-1.0.3.jar`（最新版）を `mods/` フォルダに放り込む（**クライアントのみで OK**、サーバ不要）

---

## Configuration

`config/pingtomap-client.toml` を編集（または NeoForge / Forge の Mod Settings GUI から）:

| キー | 既定 | 説明 |
|---|---|---|
| `feature.enabled` | true | マスタースイッチ |
| `feature.registerOwnPings` | true | 自分の ping も waypoint 化するか (false ならチームメイトの ping のみ) |
| `appearance.waypointLifetimeSec` | 30 | waypoint が地図に残る秒数 (-1 で永続) |
| `appearance.useTeamColor` | true | vanilla scoreboard team の色を使う (false ならシアン固定) |

---

## Compatibility

| MOD | サポート | 備考 |
|---|---|---|
| **Ping-Wheel** | required | Mixin ターゲット、必須 |
| **JourneyMap** | optional (CLIENT のみ) | waypoint 登録のターゲット、なければ静かに無視 |
| Voice Chat 系 (Plasmo Voice 等) | 影響なし | Ping-Wheel が両立してるので一緒に動く |
| Xaero's Minimap / Worldmap | 未対応 | Xaero は公式 API なし |

---

## Known Limitations

### Fabric ビルド: JourneyMap 統合 disable
**1.20.1 / 1.21.1 の Fabric ビルドのみ**、JourneyMap への waypoint 自動登録が現状 **無効**。Mixin での Ping-Wheel フックは動くが、JM への登録呼び出しがスキップされる。

理由: JourneyMap Fabric jar が要求する Loom 1.14 がまだ unreleased で、現在の Loom 1.10-SNAPSHOT では JM API がリンクできない。

将来予定: JM v1.1/v2.1 reflection bridge による迂回実装。Loom 1.14 リリースかリフレクションブリッジのどちらか早い方で解消予定。

### NeoForge 1.20.1 ビルドなし
NeoForge は 1.21+ から派生したプロジェクトのため、1.20.1 用 NeoForge ビルドは存在しない。1.20.1 で NeoForge 系を使いたい場合は Forge 1.20.1 ビルドを使ってください。

---

## FAQ

**Q. サーバ側にも MOD 入れる必要ある？**  
A. いいえ、**クライアントのみ**で動作します。Ping-Wheel 自体はサーバ要だが、P2M はクライアントで完結。

**Q. 大量に ping すると waypoint が乱立しない？**  
A. 同じプレイヤーが連続 ping した場合、古い waypoint は自動削除されます (UUID 単位で 1 つだけ保持)。

**Q. ping したけど地図に出ない！**  
A. 以下を確認:
1. Ping-Wheel と JourneyMap が両方インストールされてるか
2. `feature.enabled = true` か
3. ping 距離が Ping-Wheel 設定 (`pingDistance`) の範囲内か (デフォルト 2048 ブロック)
4. 自分の ping を表示したい場合 `feature.registerOwnPings = true`

**Q. Compass to Map と一緒に使える？**  
A. もちろん。役割が違うので衝突しません (C2M = 構造物・バイオーム発見、P2M = チーム ping)。

---

## License

[MIT License](LICENSE)

---

## Credits

- Author: KURONAMI
- Assist: Claude (Anthropic)
- Built on:
  - [Ping-Wheel](https://modrinth.com/mod/ping-wheel) by LukenSkyne
  - [JourneyMap](https://modrinth.com/mod/journeymap) by TeamJM
- Sister mod: [Compass to Map](https://github.com/KURONAMI333/compass-to-map) (EC × NC × JM addon)
