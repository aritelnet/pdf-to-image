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
 * PDF�t�@�C����ǂݍ��݁A�y�[�W���Ƃ̓ǂݎ����\�ɂ���C�e���[�^��񋟂��܂��B
 * <p>
 * ���̓t�@�C����DPI���w�肵�āA���̃N���X�� for ���ŉ񂷂��Ƃ�BufferedImage���擾���邱�Ƃ��ł��܂��B
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
	 * PDF�t�@�C���̃C���[�W��Ԃ��C�e���[�^�𐶐����܂��B
	 */
	@Override
	public Iterator<BufferedImage> iterator() {
		return new Iterator<BufferedImage>() {
			/** ���݂̃C���f�b�N�X */
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
	 * �w�肳�ꂽ�y�[�W�̉摜��ǂݎ��܂��B
	 * @param pageIndex 0����n�܂�y�[�W�ԍ�
	 * @return �ǂݎ����BufferedImage
	 * @throws IOException PDF�t�@�C�����ǂݎ��Ȃ��ꍇ
	 */
	public BufferedImage get(int pageIndex) throws IOException {
		return renderer.renderImageWithDPI(pageIndex, dpi, ImageType.RGB);
	}

	/**
	 * PDF�̃y�[�W����Ԃ��܂��B
	 * @return
	 */
	public int getNumberOfPages() {
		return numberOfPages;
	}

	/**
	 * PDF�t�@�C������܂��B
	 */
	@Override
	public void close() throws IOException {
		document.close();
	}
	
}
