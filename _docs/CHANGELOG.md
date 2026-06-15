# Changelog

All notable changes to Ping to Map (P2M) will be documented in this file.
Format: [Keep a Changelog](https://keepachangelog.com/en/1.1.0/) — [Semver](https://semver.org/)

## [Unreleased]

### Notes
- カスタム waypoint アイコンは将来予定。

## [1.2.0] - 2026-06-15

### Added
- **Minecraft 1.21.4 / 1.21.11 対応**（Fabric + NeoForge）。JourneyMap waypoint フル対応。
- Fabric 1.21.1 ビルドを **フル JourneyMap waypoint 化**（従来は ping 受信のチャット表示のみ）。NeoForge/Forge と同じ v2 API で一時 waypoint を自動登録する。

### Compatibility
- Minecraft 1.20.1 / 1.21.1 / 1.21.4 / 1.21.11 — NeoForge / Forge / Fabric — CLIENT 専用
- 1.21.4 / 1.21.11 は Fabric + NeoForge（Forge は 1.20.1 / 1.21.1）

## [1.1.0] - 2026-06-03

### Fixed
- JM waypoint が lifetime 経過後も消えない問題を修正 (issue #1)。従来は次の ping 受信時にしか期限切れ掃除が走らず、単発 ping の waypoint がマップに残り続けた。新設の `PingWaypointTicker` (Fabric はクライアント tick コールバック) が毎 client tick で掃除し、後続 ping が無くても確実に消える。logout / ワールド退出時は追跡 waypoint を全削除。

### Added
- **Fabric 版で JourneyMap 連携を実装** (1.21.1 = JM v2 API / 1.20.1 = JM v1 API)。これまで Fabric は ping 受信ログのみだったが、NeoForge/Forge と同じく一時 waypoint を自動登録するようになった。
- 全 loader で waypoint 寿命を **Ping-Wheel の pingDuration に同期** (`appearance.syncWithPingWheel`, 既定 ON)。ワールド内の ping とマップ上の waypoint が同時に消える。pingDuration ≥ 60 の永続ピンにも追従。OFF にすると固定 `appearance.waypointLifetimeSec` を使用。

### Compatibility
- Minecraft 1.21.1 / 1.20.1 — NeoForge / Forge / Fabric — CLIENT 専用

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
