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
 * PDFファイルをローカルストレージにPNG形式で書きだします。このクラスはマルチスレッドで実行することもできます。
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
		// PDFのファイル名を取得
		String fileNameWithoutExtension = getFileNameWithoutExtension(pdfFile.getName());
		
		// PDFファイルの画像化
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
	 * ファイル名から拡張子をのぞく部分を返します。拡張子が無い場合は、そのすべてを返します。
	 * 
	 * @param name ファイル名
	 * @return 拡張子をのぞいたファイル名
	 */
	private static String getFileNameWithoutExtension(String name) {
		int i = name.lastIndexOf('.');
		return i == -1 ? name : name.substring(0, i);
	}
}
