# Publishing Checklist — Ping to Map

準備中の版は **1.2.3**、既存15セル。配布先での公開状態は公開直前に直接確認する。この文書の版表記を公開済みの証拠にしない。

## 配布ファイル

JAR名は `ping-to-map-journeymap-<minecraft>-<loader>-<modversion>.jar`。
例: `ping-to-map-journeymap-1.21.1-neoforge-1.2.3.jar`。
JourneyMap版とXaero版は同じ命名形式を使う。MOD ID・namespace・版番号は変更しない。

| loader | Minecraft |
|---|---|
| neoforge | 1.21.1, 1.21.11, 1.21.4, 1.21.8, 26.1.2, 26.2 |
| forge | 1.20.1, 1.21.1 |
| fabric | 1.20.1, 1.21.1, 1.21.11, 1.21.4, 1.21.8, 26.1.2, 26.2 |

## 適用条件

Client only。Ping-Wheelは必須、地図MODはmetadata上optional。FabricはForge Config API Portも使用する。
Forge 1.20.1はJourneyMap 5.10.5のv1 APIで照合済み。Fabric 1.20.1と新世代はJourneyMap 6.x。Fabric 1.20.1の連携も実装済みで、旧「JM無効・チャットのみ」の制限は現行版には当てはまらない。Forge 1.20.1のJM6と全ホストpatchの組合せは未確認。

正式15セルのclean build、JAR metadata・CRC・class・source binding、実ホストAPIの照合は実施済み。実ゲーム起動・マルチプレイ・GUIは今回未実施で、この文書は成功を主張しない。今回のファイル名変更ではJARの内容を変更せず、全セルの旧新SHAとentry CRC一致を検査する。改名だけを理由に全セルの再ビルド・GUI確認を追加しない。

## 公開前チェック

- [ ] 現在の承認範囲と既知バグの閉鎖を確認する。
- [ ] 上記JAR名とloader/MC/版・SHAを完成manifestへ照合する。正式ビルドの再実施が必要な変更では `--no-build-cache` を使用する。
- [ ] JARにLICENSEが入り、compileOnly/localRuntimeの他MODを同梱していないことを確認する。
- [ ] README・CHANGELOG・保存ストア本文と版別条件を揃える。
- [ ] 配布先の同版衝突、MC/loader/Java/client環境、版別依存を確認する。CF RelationsへFabric APIは追加しない。
- [ ] canonical作者名義と公開物の内容を確認する。Sourceリンクは維持し、不具合窓口はCurseForgeコメントまたはXの @kuronami333 のDMとする。

## ビルドと公開資料

通常の正式ビルドは各セルで `build --no-build-cache` を使用する。対応表・JAR名・版別依存と変更内容を照合してから、配布先のタグと説明を揃える。不具合の報告先はCurseForgeのコメントまたはXの @kuronami333 のDM。
