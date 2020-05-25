package net.aritel.tools.pdf2image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.google.gson.Gson;

import net.aritel.tools.pdf2image.converter.PdfDecorder;

/**
 * PDF ファイルから画像を生成するプログラムのエントリポイントです。
 * 
 * @author aritelnet
 */
public class Pdf2ImageExecutable {
	
	public static class ResultJson {
		public String status;
		public int errorCode;
		public String message;
		public List<String> fileNames = new ArrayList<>();
	}
	
	public static void main(String[] args) {
		ResultJson rj = new ResultJson();
		if (args.length < 2) {
			rj.status = "Error";
			rj.errorCode = 1;
			rj.message = "Usage: Pdf2ImageExecutable <InputPdfPath> <OutputDir> [<dpi>]";
			System.out.print(new Gson().toJson(rj));
			return;
		}
		
		File inputPdfPath = new File(args[0]);
		File outputDir = new File(args[1]);
		int dpi;
		try {
			dpi = args.length >=3 ? Integer.parseInt(args[2]) : 300;
		} catch (NumberFormatException e) {
			rj.status = "Error";
			rj.errorCode = 2;
			rj.message = "Invalid dpi value `" + args[2] + "`.";
			System.out.print(new Gson().toJson(rj));
			return;
		}
		
		// PDFのファイル名を取得
		String fileNameWithoutExtension = getFileNameWithoutExtension(inputPdfPath.getName());
		
		// PDFファイルの画像化
		try (PdfDecorder decorder = new PdfDecorder(inputPdfPath, dpi)) {
			int i = 0;
			for (BufferedImage bi : decorder) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(bi, "png", baos);
				File file = new File(outputDir, fileNameWithoutExtension + "_" + i + ".png");
				Files.write(file.toPath(), baos.toByteArray());
				rj.fileNames.add(file.getAbsolutePath());
			}
		} catch (IOException e) {
			rj.status = "Error";
			rj.errorCode = 3;
			rj.message = "IOException occured.";
			System.out.print(new Gson().toJson(rj));
			return;
		}
		
		rj.status = "Success";
		System.out.print(new Gson().toJson(rj));
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
