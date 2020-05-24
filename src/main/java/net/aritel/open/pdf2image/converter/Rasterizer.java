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
 * PDF�X�g���[����JPEG�ɕϊ����܂��B
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
	
	/**
	 * PDF�t�@�C�����y�[�W�������ďo�̓X�g���[���ɏ����o���܂��B
	 * @param in PDF�t�@�C���̓��̓X�g���[��
	 * @param op �y�[�W���Ƃ̏o�̓X�g���[���̃v���o�C�_
	 * @param dpi �摜����DPI
	 * @param formatName �摜���̃t�H�[�}�b�g
	 * @throws IOException PDF���ǂ߂Ȃ�������A�摜�o�͂ŃG���[���N�����ꍇ
	 */
	public void resterize(InputStream in, OutputStreamProvider op, int dpi, String formatName) throws IOException {
		try (PDDocument doc = PDDocument.load(in)) {
			int numberOfPages = doc.getNumberOfPages();
			PDFRenderer renderer = new PDFRenderer(doc);
			for (int i = 0; i < numberOfPages; i++) {
				BufferedImage bi = renderer.renderImageWithDPI(i, dpi, ImageType.RGB);
				try (OutputStream os = op.get(i)) {
					ImageIO.write(bi, formatName, os);
				}
			}
		}
	}
}
