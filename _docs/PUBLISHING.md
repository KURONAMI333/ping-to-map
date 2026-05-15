# Publishing Checklist — Ping to Map (P2M)

Modrinth / CurseForge への公開・更新手順。

> このファイルは以前 Compass to Map の PUBLISHING.md をコピーしたまま
> P2M 用に更新されていなかった（2026-05-15 修正）。以下は P2M の事実に
> 即した内容。

現状: **v1.0.3 公開済み**（Modrinth `ping-to-map` / CurseForge `ping-to-map`）。
以下は更新リリース時に使う生きたチェックリスト。

---

## 公開前チェックリスト

### コード・ビルド

- [ ] `mod_version` を `gradle.properties` で更新（semver）
- [ ] 各サブプロジェクトで `./gradlew clean build`（NeoForge 1.21.1 = ルート、
      `forge-1.20.1` / `fabric-1.21.1` / `fabric-1.20.1`）
- [ ] 各 `build/libs/pingtomap-x.y.z.jar` のサイズ確認（localRuntime 混入なし）
- [ ] `./gradlew runClient` で実機確認
  - [ ] Ping-Wheel で ping → JM に一時 waypoint 出現（シアン/チームカラー）
  - [ ] 設定秒数で自動消滅
  - [ ] 同一プレイヤー連続 ping → 古い waypoint が置き換わる（UUID 単位 1 つ）
  - [ ] Fabric ビルドは JM 統合 disable・チャット通知のみ（既知制限どおり）

### ドキュメント

- [ ] `README.md` の機能 / 設定 / 互換 / インストール（jar 名 `pingtomap-x.y.z.jar`）が最新
- [ ] `LICENSE` 存在
- [ ] `_docs/ROADMAP.md` / `_docs/CHANGELOG.md` 更新
- [ ] バージョン番号が各所一致（`gradle.properties`・mods.toml/fabric.mod.json・README）

### メタデータ

- [ ] `displayURL` / `issueTrackerURL` が `github.com/KURONAMI333/ping-to-map`
- [ ] `description` が現状機能と合致
- [ ] `authors=KURONAMI` / `license=MIT`

---

## ストア事実（説明文・タグの基準）

| 項目 | 値 |
|---|---|
| Project name | `Ping to Map` |
| Slug | `ping-to-map` |
| Mod ID | `pingtomap` / package `com.kuronami.pingtomap` |
| Loaders × MC | NeoForge/Forge/Fabric × 1.21.1、Forge/Fabric × 1.20.1（**NeoForge 1.20.1 なし**） |
| Environment | **Client only**（サーバ不要） |
| 依存 | **Ping-Wheel（必須）** / JourneyMap（任意・推奨、Forge/NeoForge のみ）/ Fabric は Forge Config API Port |
| 既知制限 | Fabric ビルドは JM 統合 disable（Loom 1.14 未リリース）。チャット通知のみ |
| License | MIT |

> Description 本文は `README.md` をそのままコピペ（Modrinth は Markdown 可、
> CurseForge は BBCode/HTML 変換）。jar 実ファイル名は `pingtomap-<version>.jar`
> （ローダー/MC はストアのタグで区別、ファイル名に接尾辞は付かない）。

---

## 更新リリース手順

1. `gradle.properties` の `mod_version` を更新
2. `_docs/CHANGELOG.md` 更新
3. 全サブプロジェクト `./gradlew clean build`
4. 実機で動作確認
5. Modrinth: Versions タブから各ローダー/MC の jar をアップロード（タグ付与）
6. CurseForge: Files タブから同上
7. GitHub: `vX.Y.Z` タグ付きリリース作成
