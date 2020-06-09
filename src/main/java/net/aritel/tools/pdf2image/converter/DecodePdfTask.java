package net.aritel.tools.pdf2image.converter;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;

/**
 * PDF�t�@�C�������[�J���X�g���[�W��PNG�`���ŏ��������܂��B���̃N���X�̓}���`�X���b�h�Ŏ��s���邱�Ƃ��ł��܂��B
 * 
 * @author aritelnet
 */
public class DecodePdfTask implements Callable<File[]> {
	
	private File pdfFile;
	private File outputDir;
	private int dpi;
	private FileType fileType = FileType.PNG;
	
	public enum FileType {
		PNG("png", "png"),
		JPEG("jpeg", "jpg");
		String formatName;
		String fileExtension;
		public String getFormatName() {
			return formatName;
		}
		public String getFileExtension() {
			return fileExtension;
		}
		FileType(String formatName, String fileExtension) {
			this.formatName = formatName;
			this.fileExtension = fileExtension;
		}
	}
	
	public DecodePdfTask(File pdfFile, File outputDir, int dpi) {
		this.pdfFile = pdfFile;
		this.dpi = dpi;
		this.outputDir = outputDir;
	}
	
	public DecodePdfTask(File pdfFile, File outputDir, int dpi, FileType fileType) {
		this(pdfFile, outputDir, dpi);
		this.fileType = fileType;
	}
	
	@Override
	public File[] call() throws IOException {
		// PDF�̃t�@�C�������擾
		String fileNameWithoutExtension = getFileNameWithoutExtension(pdfFile.getName());
		
		// PDF�t�@�C���̉摜��
		List<File> fileList = new ArrayList<>();
		try (PdfDecorder decorder = new PdfDecorder(pdfFile, dpi);
				ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			int i;
			Iterator<BufferedImage> itr;
			FileType ft = this.fileType;
			for (i = 0, itr = decorder.iterator(); itr.hasNext(); i++) {
				baos.reset();
				ImageIO.write(itr.next(), ft.getFormatName(), baos);
				File file = new File(outputDir, fileNameWithoutExtension + "_" + i + "." + ft.getFileExtension());
				Files.write(file.toPath(), baos.toByteArray());
				fileList.add(file);
			}
		}
		return fileList.toArray(new File[fileList.size()]);
	}

	/**
	 * �t�@�C��������g���q���̂���������Ԃ��܂��B�g���q�������ꍇ�́A���̂��ׂĂ�Ԃ��܂��B
	 * 
	 * @param name �t�@�C����
	 * @return �g���q���̂������t�@�C����
	 */
	private static String getFileNameWithoutExtension(String name) {
		int i = name.lastIndexOf('.');
		return i == -1 ? name : name.substring(0, i);
	}
}
