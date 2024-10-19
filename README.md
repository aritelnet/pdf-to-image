# pdf-to-image
Convert PDF files to image format.

## What's this
PDFファイルやバイナリから、画像ファイルやBufferedImageを生成します。
PdfBox をラップした簡単なライブラリです。

## Example

```
> java -jar pdf-to-image-0.0.3.jar <InputPdfPath> <OutputDir> [<dpi>] [<formatType>]
```

出力フォルダにPNG形式のファイルが格納されます。コンソールにはJSONで生成されたファイル名が出力されますので、jqコマンドなどで解析したら後続処理に使えそうです。
formatType には PNG か JPEG を指定できます。
