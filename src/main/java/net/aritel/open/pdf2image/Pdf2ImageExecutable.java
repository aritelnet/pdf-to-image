package net.aritel.open.pdf2image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;

import net.aritel.open.pdf2image.converter.Rasterizer;

/**
 * PDF ファイルから画像を生成するプログラムのエントリポイントです。
 * 
 * @author aritelnet
 */
public class Pdf2ImageExecutable {

	public static void main(String[] args) throws FileNotFoundException, IOException, URISyntaxException {
		Rasterizer r = new Rasterizer();
		try (InputStream fis = new URL("http://www.africau.edu/images/default/sample.pdf").openStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			r.rasterize(fis, baos);
			Files.write(new File("./test.jpeg").toPath(), baos.toByteArray());
		}
		
	}

}
