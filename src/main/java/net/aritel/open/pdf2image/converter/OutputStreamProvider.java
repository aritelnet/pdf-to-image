package net.aritel.open.pdf2image.converter;

import java.io.OutputStream;

/**
 * このインターフェースはPDFのページごとの出力ストリームを生成します。
 * 
 * @author aritelnet
 */
public interface OutputStreamProvider {
	public OutputStream get(int pageNumber);
}
