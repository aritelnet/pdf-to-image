package net.aritel.tools.pdf2image.converter;

import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

/**
 * PDFファイルを読み込み、ページごとの読み取りを可能にするイテレータを提供します。
 * <p>
 * 入力ファイルとDPIを指定して、このクラスを for 文で回すことでBufferedImageを取得することができます。
 * 
 * @author aritelnet
 *
 */
public class PdfDecorder implements Iterable<BufferedImage>, Closeable {

	private PDDocument document;
	private PDFRenderer renderer;
	private int dpi;
	private int numberOfPages;
	
	public PdfDecorder(File file, int dpi) throws IOException {
		this.document = PDDocument.load(file);
		this.numberOfPages = document.getNumberOfPages();
		this.renderer = new PDFRenderer(document);
		this.dpi = dpi;
	}
	
	public PdfDecorder(InputStream is, int dpi) throws IOException {
		this.document = PDDocument.load(is);
		this.numberOfPages = document.getNumberOfPages();
		this.renderer = new PDFRenderer(document);
		this.dpi = dpi;
	}
	
	/**
	 * PDFファイルのイメージを返すイテレータを生成します。
	 */
	@Override
	public Iterator<BufferedImage> iterator() {
		return new Iterator<BufferedImage>() {
			/** 現在のインデックス */
			int pageIndex = 0;
			@Override
			public boolean hasNext() {
				return pageIndex < numberOfPages;
			}

			@Override
			public BufferedImage next() {
				if (!hasNext()) throw new NoSuchElementException();
				try {
					return get(pageIndex++);
				} catch (IOException e) {
					return null;
				}
			}
		};
	}
	
	/**
	 * 指定されたページの画像を読み取ります。
	 * @param pageIndex 0から始まるページ番号
	 * @return 読み取ったBufferedImage
	 * @throws IOException PDFファイルが読み取れない場合
	 */
	public BufferedImage get(int pageIndex) throws IOException {
		return renderer.renderImageWithDPI(pageIndex, dpi, ImageType.RGB);
	}

	/**
	 * PDFのページ数を返します。
	 * @return
	 */
	public int getNumberOfPages() {
		return numberOfPages;
	}

	/**
	 * PDFファイルを閉じます。
	 */
	@Override
	public void close() throws IOException {
		document.close();
	}
	
}
