package net.aritel.open.pdf2image.converter;

import java.io.OutputStream;

/**
 * ���̃C���^�[�t�F�[�X��PDF�̃y�[�W���Ƃ̏o�̓X�g���[���𐶐����܂��B
 * 
 * @author aritelnet
 */
public interface OutputStreamProvider {
	public OutputStream get(int pageNumber);
}
