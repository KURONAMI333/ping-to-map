# Publishing Checklist

Modrinth / CurseForge への公開手順。

---

## 公開前チェックリスト

### コード・ビルド

- [ ] `mod_version` を `gradle.properties` で更新（semver: 1.0.0 等）
- [ ] `./gradlew clean build` でクリーンビルド
- [ ] `build/libs/compasstomap-x.y.z.jar` のサイズ確認（テスト用 localRuntime が混入してないか、約 30〜50KB 程度）
- [ ] `./gradlew runClient` で実機動作確認
  - [ ] Explorer's Compass で構造物検索 → 発見 → JM waypoint 自動登録
  - [ ] チャット通知（OP 状態 = クリック可能、非 OP = 装飾なし）
  - [ ] 同じ構造物を再検索 → dedupe（再登録されない）
  - [ ] 別の構造物を検索 → 新 waypoint 追加
  - [ ] カテゴリ別色分け（村/要塞/ダンジョン等）

### ドキュメント

- [ ] `README.md` 機能リスト / 設定 / 互換 / FAQ が最新
- [ ] `LICENSE` が存在
- [ ] `_docs/ROADMAP.md` 更新
- [ ] バージョン番号が各所一致（`gradle.properties`, `mods.toml` template, README）

### メタデータ

- [ ] `neoforge.mods.toml` の `displayURL` / `issueTrackerURL` が GitHub repo URL に
- [ ] `description` が現状機能と合致
- [ ] `authors`, `license` が正しい

---

## Modrinth 初回公開手順

1. https://modrinth.com/dashboard/new-project でプロジェクト作成
2. 必須情報入力:
   - **Project name**: `Compass to Map`
   - **Project ID/slug**: `compass-to-map`
   - **Project type**: `Mod`
   - **Summary**: README の冒頭 1 行コンセプト
3. アイコン (256x256 推奨)、ギャラリー画像をアップロード
4. **Description** に README をコピペ（Markdown 対応）
5. **Categories** にチェック:
   - `utility`, `adventure`, `optimization`
6. **Game versions**: 1.21.1
7. **Loaders**: NeoForge
8. **Environment**:
   - Client: `required`
   - Server: `required`
9. **License**: `MIT`
10. **Links**:
    - Source: GitHub repo URL
    - Issues: GitHub Issues URL
11. プロジェクト作成 → Submit for review (Modrinth 承認、数時間〜数日)
12. 承認後、Versions タブから新バージョンをアップロード:
    - **Version**: `1.0.0` / `1.0.0 - Initial release`
    - **Release type**: `Release`
    - **Game versions**: 1.21.1 / **Loaders**: NeoForge
    - **Files**: `compasstomap-1.0.0.jar`
    - **Dependencies**:
      - Explorer's Compass (required)
      - JourneyMap (optional, recommended)

---

## CurseForge 初回公開手順

1. https://www.curseforge.com/project/create でプロジェクト作成
2. 入力:
   - **Game**: Minecraft
   - **Project Type**: Mods
   - **Project Name**: Compass to Map
   - **Slug**: compass-to-map
3. カテゴリ:
   - Map and Information / Utility & QoL / Adventure and RPG
4. **Description** (BBCode/HTML、Markdown 不可) に README を変換コピペ
5. **License**: MIT
6. **Game Versions** タグ: 1.21.1 / **Mod Loaders**: NeoForge
7. プロジェクト作成 → 承認待ち
8. 承認後、File Upload:
   - **File**: `compasstomap-1.0.0.jar`
   - **Display Name**: `1.0.0 - Initial release`
   - **Release Type**: `Release`
   - **Game Versions**: 1.21.1 / **Mod Loader**: NeoForge
   - **Optional Dependencies**: Explorer's Compass, JourneyMap

---

## 更新リリース時 (1.0.0 → 1.0.1)

1. `gradle.properties` の `mod_version=1.0.1`
2. `_docs/CHANGELOG.md` 更新
3. `./gradlew clean build`
4. 実機で動作確認
5. Modrinth: Versions タブから新バージョンアップロード
6. CurseForge: Files タブから新ファイルアップロード
7. GitHub: タグ付きリリース (`v1.0.1`) を作成
