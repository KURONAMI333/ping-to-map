# Changelog

All notable changes to Ping to Map (P2M) will be documented in this file.
Format: [Keep a Changelog](https://keepachangelog.com/en/1.1.0/) — [Semver](https://semver.org/)

## [Unreleased]

### Notes
- v2 (Fabric backport, custom waypoint icons) は将来予定。

## [1.0.0] - YYYY-MM-DD (未公開)

### Added
- Ping-Wheel の `PingManager.acceptPingPacket` を **Mixin** でフックして、ping 受信時に JM 一時 waypoint を自動登録
- waypoint 表示名: `📍 {playerName}'s Ping`
- 一時 waypoint: デフォルト 30 秒で自動削除 (Config で 1〜600 秒 or 永続に変更可)
- vanilla scoreboard team の色を waypoint 色に反映 (Config で OFF にするとシアン固定)
- 同一プレイヤーの連続 ping は古い waypoint を上書き (UUID で識別)
- Config: feature.enabled / feature.registerOwnPings / appearance.waypointLifetimeSec / appearance.useTeamColor
- 22 言語 lang ファイル (config GUI 用、チャット通知なし)

### Compatibility
- Minecraft 1.21.1
- NeoForge 21.1+
- **CLIENT 専用 MOD** (サーバ側に入れる必要なし)
- Required: Ping-Wheel by LukenSkyne (Mixin ターゲット)
- Optional: JourneyMap (1.21 系、未導入でも crash しない)

### Notes
- **Mixin 使用** (Ping-Wheel が公式 API を持たないため)
- @Inject で HEAD に割り込み、Ping-Wheel 本来の処理は止めない
- Inner class isolation で JM 不在環境でも crash しない
- Sister mod: Compass to Map (EC × NC × JM addon)
