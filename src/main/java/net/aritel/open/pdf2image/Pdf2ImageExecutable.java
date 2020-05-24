package net.aritel.open.pdf2image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;

import com.google.gson.Gson;

import net.aritel.open.pdf2image.converter.Rasterizer;

/**
 * PDF ファイルから画像を生成するプログラムのエントリポイントです。
 * 
 * @author aritelnet
 */
public class Pdf2ImageExecutable {

	public static void main(String[] args) throws FileNotFoundException, IOException, URISyntaxException {
		ResultJson rj = new ResultJson();
		Rasterizer r = new Rasterizer();
		try (InputStream fis = new URL("http://www.africau.edu/images/default/sample.pdf").openStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			class BAOSWrapper extends ByteArrayOutputStream {
				int pageNumber;
				public BAOSWrapper(int pageNumber) {
					this.pageNumber = pageNumber;
				}
				@Override
				public void close() throws IOException {
					super.close();
					Files.write(new File("./test_" + pageNumber + ".jpg").toPath(), this.toByteArray());
					rj.addFileNames("./test_" + pageNumber + ".jpg");
				}
			}
			r.resterize(fis, p -> {
				BAOSWrapper os = new BAOSWrapper(p);
				return os;}, 300, "JPEG");
			
		}
		System.out.print(new Gson().toJson(rj));
	}

}
