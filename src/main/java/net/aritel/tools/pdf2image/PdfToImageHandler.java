package net.aritel.tools.pdf2image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;

import net.aritel.tools.pdf2image.converter.DecodePdfTask.FileType;
import net.aritel.tools.pdf2image.converter.PdfDecorder;

public class PdfToImageHandler implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
        // ロガーを定義
        LambdaLogger logger = context.getLogger();
        logger.log("EVENT TYPE: " + event.getClass().toString());

        // フォントファイルを含めるため、強制的にホームディレクトリを変更
        // (PDFBox でホームディレクトリの .fonts を含めている)
        System.setProperty("user.home", "/var/task/");

        // ボディを取得
        String body = event.getBody(); // string or null
        if (body == null) {
            APIGatewayV2HTTPResponse response = new APIGatewayV2HTTPResponse();
            response.setStatusCode(400);
            response.setBody("Body is empty.");
            return response;
        }
        byte[] pdfBytes;
        if (event.getIsBase64Encoded()) {
            pdfBytes = java.util.Base64.getDecoder().decode(body);
        } else {
            pdfBytes = body.getBytes(StandardCharsets.ISO_8859_1);
        }
        logger.log("LENGTH " + pdfBytes.length);
        // ヘッダからx-dpiとx-formatを取得
        int dpi;
        try {
            dpi = Integer.parseInt(event.getHeaders().get("x-dpi"));
        } catch (Exception e) {
            dpi = 300;
        }
        FileType fileType;
        try {
            fileType = FileType.valueOf(event.getHeaders().get("x-format"));
        } catch (Exception e) {
            fileType = FileType.PNG;
        }
        // PDFファイルの画像化
        byte[] zipBytes = null;
        try {
            // 出力Zipファイル
            try (ByteArrayOutputStream zbaos = new ByteArrayOutputStream()) {
                // PDFデコーダ
                try (ZipOutputStream zos = new ZipOutputStream(zbaos);
                        PdfDecorder decorder = new PdfDecorder(new ByteArrayInputStream(pdfBytes), dpi);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    int i;
                    Iterator<BufferedImage> itr;
                    FileType ft = fileType;
                    for (i = 0, itr = decorder.iterator(); itr.hasNext(); i++) {
                        baos.reset();
                        ImageIO.write(itr.next(), ft.getFormatName(), baos);
                        zos.putNextEntry(new ZipEntry(i + "." + ft.getFileExtension()));
                        zos.write(baos.toByteArray());
                    }
                }
                zipBytes = zbaos.toByteArray();
            }
        } catch (IOException e) {
            logger.log(e.getMessage());
            APIGatewayV2HTTPResponse response = new APIGatewayV2HTTPResponse();
            response.setStatusCode(400);
            response.setBody("Error");
            return response;
        }

        // レスポンスオブジェクトを作成
        APIGatewayV2HTTPResponse response = new APIGatewayV2HTTPResponse();
        response.setStatusCode(200);
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/zip");
        response.setHeaders(headers);
        response.setIsBase64Encoded(true);
        response.setBody(java.util.Base64.getEncoder().encodeToString(zipBytes));
        return response;
    }
}
