package net.aritel.tools.pdf2image;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import net.aritel.tools.pdf2image.converter.DecodePdfTask;

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
		
		// PDFファイルの画像化
		try {
			DecodePdfTask task = new DecodePdfTask(inputPdfPath, outputDir, dpi);
			for (File f : task.call()) {
				rj.fileNames.add(f.getAbsolutePath());
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

}
