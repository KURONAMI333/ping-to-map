# Compass to Map - ROADMAP

## ✅ Phase 1: コア機能 (v1.0)

- [x] ExplorersCompass の DataComponent 監視 (`STRUCTURE_ID_COMPONENT`, `COMPASS_STATE_COMPONENT`, `FOUND_X/Z_COMPONENT`)
- [x] サーバ側 PlayerTickEvent.Post で発見検知
- [x] サーバ→クライアント `StructureFoundPayload`
- [x] JourneyMap Plugin (`@JourneyMapPlugin` + `IClientPlugin`)
- [x] クライアント側で `WaypointFactory.createClientWaypoint` + `IClientAPI.addWaypoint`
- [x] 構造物カテゴリ別色分け (バニラ 10 カテゴリ + テンプル系/海賊系拡張)
- [x] **MOD 追加構造物への hash 色生成** (HSL ベース、未分類でも一意色)
- [x] `prettifyStructureName` で見やすい waypoint 名
- [x] **dedupe (dim + structureId + x + z) を Set で履歴保持** (A→B→A 再発見も防ぐ)
- [x] **Y 座標フォールバック** (Heightmap 未ロード時に dimension/構造物種別ごとの安全な Y、奈落落ち防止)
- [x] **チャット表示は X, Z のみ** (Y は構造物の実位置とズレるため非表示)
- [x] OP 限定の `/tp` 提案 (非OPには装飾なし)
- [x] PlayerLoggedOutEvent でリーク防止 (SEEN_KEYS Set もクリア)
- [x] try-catch ガード (EC API 不一致時の永久サスペンド)
- [x] mods.toml: EC required `[0,)` (Maven ハイフン比較罠回避) / JM optional `side="CLIENT"` / NeoForge `[21.1,)` (パッチで切らない)
- [x] Config: 4 項目 (enabled / notifyOnFound / colorByCategory / persistentWaypoints)
- [x] 22 言語 lang ファイル (en / ja は完全、他 20 言語は機械翻訳ベース)
- [x] LICENSE (MIT)
- [x] README
- [x] **JM 不在テスト合格** (NoClassDefFoundError なし、静かに無視)

## ✅ Phase 2 (v2.0): Nature's Compass 対応

- [x] **Nature's Compass 対応** (バイオーム発見も waypoint 化、同作者で API 同形を実機確認)
- [x] **バイオーム専用カラーマッピング** (vanilla 14 カテゴリ + MOD は hash 色)
- [x] **NC 不在環境耐性** (Inner class isolation 二重適用)
- [x] **Config 個別 ON/OFF** (`feature.enableStructure` / `feature.enableBiome`)
- [x] **22 言語へ biome 翻訳追加**
- [x] **SEEN_KEYS の容量制限 LRU** (上限 512 件、code-reviewer 指摘で v1.0 で完了済み)

## 🔮 Phase 3 (v2.x): UX 改善

- [ ] **`/compasstomap clear` コマンド** (OP 限定、自分の登録 waypoint 一括削除)
- [ ] **構造物別の有効/無効化 Config** (例: 村は登録しない、要塞だけ登録)
- [ ] **訪問済み waypoint の灰色化** (一定距離内に近づいたら色を薄く)
- [ ] **waypoint 名のカスタムフォーマット** (Config で `%name%` `%coords%` 等)
- [ ] **CLIENT config 分割** (COLOR_BY_CATEGORY / PERSISTENT_WAYPOINTS は CLIENT 側へ)
- [ ] **Locale 対応の toLowerCase** (`Locale.ROOT` 指定)
- [ ] **namespace prefix** (modded 構造物の名前衝突対策)
- [ ] **チャンク強制ロード or async 高さ取得** (Y 座標を構造物の実位置に近づける)

## 🌍 Phase 4 (v3.0): マルチローダー対応

- [ ] **Forge 1.21.1** バックポート (Architectury 採用)
- [ ] **Fabric 1.21.1** 対応 (EC / NC は Fabric 版あり、JM も対応)
- [ ] **マルチバージョン**: 1.20.1 backport

## 🚀 Phase 5 (v4.0): 公開・コミュニティ

- [ ] スクリーンショット / GIF
- [ ] Modrinth / CurseForge 公開
- [ ] Crowdin 連携で 22 言語の native 翻訳募集
- [ ] Discord / Wiki

---

## 設計判断の記録

| 判断 | 理由 |
|---|---|
| サーバ側 PlayerTickEvent で監視 | EC は ItemStack DataComponent に検出結果を保持、サーバから観測可。Mixin/AT 不要で済む。 |
| Inner class isolation (JM API 参照) | JM 不在環境でも NoClassDefFoundError しない |
| dedupe 必須 (dimension 込み) | 別ディメンションの同座標構造物の誤マッチ防止 |
| ecApiBroken フラグ | EC API 不一致時に毎 tick × 全プレイヤーのログ汚染を防ぐ |
| OP 限定 TP 提案 | サバイバルプレイヤーに「TP できる」誤期待を与えない |
| Xaero 非対応 | Xaero に公式 API なし、ファイル直書きは shutdown 時に上書きで消える (Gravely 開発で実証) |
| MIT ライセンス | modpack 採用しやすい |
