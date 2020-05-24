package net.aritel.open.pdf2image.converter;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

/**
 * PDFストリームをJPEGに変換します。
 * 
 * @author aritelnet
 * @see https://qiita.com/ota-meshi/items/812ebffa319b2bbde1c3
 */
public class Rasterizer {
	public void rasterize(InputStream in, OutputStream out) throws IOException {
		try (PDDocument doc = PDDocument.load(in)) {
			PDFRenderer pdfRenderer = new PDFRenderer(doc);
			BufferedImage bi = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);
			ImageIO.write(bi, "JPEG", out);
		}
	}
}
