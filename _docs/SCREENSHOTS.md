# Screenshots / GIF 撮影シナリオ

Modrinth / CurseForge / README に貼る画像の撮影ガイド。

## 撮影前準備

- 解像度: **1920x1080** で撮影、必要なら後で 1280x720 にリサイズ
- HUD: 必要な部分（Explorer's Compass 検出メッセージ、JourneyMap）を残して F1 で他は消す
- 周囲の景観を整える（草原・森等の見栄え重視）
- ShareX または Windows Game Bar (`Win+G`) でスクショ/録画

## 必須スクショ (公開時に最低限欲しい)

### 1. ヒーロー画像（Modrinth ページバナー、1920×300 程度）
- 場面: コンパス + JM ミニマップ + waypoint がフレームに収まる構図
- メッセージで「Discovered Village Plains at 紫色座標」が見える状態

### 2. 構造物検索 GIF
- Explorer's Compass を右クリック → 構造物選択 → 検索開始 → 発見 → JM に紫 waypoint 自動登録 (5〜8秒)
- 「自動」感が伝わる

### 3. JourneyMap waypoint 一覧
- M キーでフルスクリーンマップ → 登録された waypoint 群
- 構造物カテゴリ別色分け（村=黄、要塞=紫、ダンジョン=赤等）が見える

### 4. チャット通知（OP 版）
- 「Discovered Ancient City at 176, -64, -1408」のチャット
- 座標が紫アンダーライン、ホバーで「Click to insert /tp command」

### 5. チャット通知（非 OP 版）
- 同じ構造物発見時のメッセージで、座標が紫色のみ（クリック不可、装飾なし）
- 「OP 限定機能」の証拠

## 任意 (あると尖る)

### 6. 設定画面
- NeoForge mod settings GUI を開いて Compass to Map の項目を見せる

### 7. dedupe 動作
- 同じ構造物を再検索 → JM に新規 waypoint が追加されない

### 8. 異なる構造物カテゴリのコンパクトショット
- 村（黄）/ 要塞（紫）/ 海洋神殿（シアン）/ ネザー要塞（オレンジ）等が並んだ JM 画面

## ファイル整理

- `screenshots/` フォルダにまとめる
- ファイル名: `01_hero.png` / `02_search_gif.gif` / `03_waypoints.png` / 等
- README から相対リンクで参照
