package net.aritel.open.pdf2image;

import java.util.ArrayList;
import java.util.List;

public class ResultJson {
	public List<String> fileNames = new ArrayList<>();
	public void addFileNames(String fileName) {
		fileNames.add(fileName);
	}
}
