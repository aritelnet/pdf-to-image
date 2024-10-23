# pdf-to-image
Convert PDF files to image format.

## What's this
PDFファイルやバイナリから、画像ファイルやBufferedImageを生成します。
PdfBox をラップした簡単なライブラリです。

API Gateway + AWS Lambda としてデプロイすることで、API サービスとして利用することができます。

## Develop and Deploy

### 前提条件
- vscode が必要です。（Eclipseプロジェクトは外しました）
- ホームディレクトリに github の鍵（~/.ssh）と、AWS のクレデンシャル（ ~/.aws）が必要です。
    - これらのディレクトリがないと devcontainer の起動に失敗します。

- VSCode (devcontainer) で開発とデプロイができます。

```bash
# Git クローンします
git clone https://github.com/aritelnet/pdf-to-image.git
# ブランチを変更します
git checkout dev/aws_lambda
# vscode を起動します
vscode .
```

- VSCode で devcontainer としてReOpenします。
    - JDK と NodeJS が入ったコンテナになります。

- AWS に構成する場合、以下のコマンドで CloudFormation (CDK) でデプロイします。
    - 現在は Stack 名を `PdfToImageStack` で固定しています。

```bash
bash deploy.sh
```

### デプロイされるリソース
CDKにより、通常は、代表的なものとして以下のリソースが追加されます。

- LambdaS3Policy (AWS::IAM::ManagedPolicy)
    - Lambda 関数から S3 バケットに PutObject を行います。
    - 全てのバケット (Resources: [*]) を対象としたポリシーのため、セキュリティに注意してください。
- LambdaExecutionRole (AWS::IAM::Role)
    - Lambda 関数の実行ロール
- PdfToImageFunction (AWS::Lambda::Function)
    - Lambda 関数
- PdfToImageHttpApi (AWS::ApiGatewayV2::Api)
    - API Gateway (HTTP API)

## Example
- curl コマンドを使ってリクエストを送る例です。

| ヘッダ             | 値の例          | 説明                                                                                           | 
| ------------------ | --------------- | ---------------------------------------------------------------------------------------------- | 
| x-dpi              | 300             | 画像のDPI（省略時は 300）                                                                      | 
| x-format           | png             | 出力画像フォーマット（png または jpeg）                                                        | 
| x-export-s3-bucket | hogehoge-bucket | 結果をS3に書き込む場合のバケット名（このヘッダを指定しない場合はHTTPレスポンスとして返します） | 
| x-export-s3-key    | test/hogehoge.zip | 結果をS3に書き込む場合のキー名 |

```bash
# 結果をS3に書き込む場合
curl -H "x-export-s3-bucket: hogehoge-bucket" -H "x-export-s3-key: test/hogehoge.zip" --data-binary @./PDF_sample.pdf https://xxxxxxxx.execute-api.us-east-1.amazonaws.com/pdf-to-image 
# 結果をレスポンスとして受け取る場合
curl --data-binary @./PDF_sample.pdf https://xxxxxxxx.execute-api.us-east-1.amazonaws.com/pdf-to-image --output hogehoge.zip
```

### 生成される zip ファイルについて
- zip ファイルには、`{0始まりのページ番号}.png` という名前で画像が保存されています。
