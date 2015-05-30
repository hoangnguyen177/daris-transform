package transform.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public class ZipUtil {

	public static final byte[] MAGIC = { 'P', 'K', 0x3, 0x4 };

	/**
	 * The method to test if a input stream is a zip archive.
	 * 
	 * @param in
	 *            the input stream to test.
	 * @return
	 */
	public static boolean isZipStream(InputStream in) throws Throwable {

		if (!in.markSupported()) {
			throw new IOException("The stream does not support mark.");
		}
		boolean isZip = true;
		try {
			in.mark(MAGIC.length);
			for (int i = 0; i < MAGIC.length; i++) {
				if (MAGIC[i] != (byte) in.read()) {
					isZip = false;
					break;
				}
			}
			in.reset();
		} catch (IOException e) {
			isZip = false;
		}
		return isZip;
	}

	/**
	 * Test if a file is a zip file.
	 * 
	 * @param f
	 *            the file to test.
	 * @return
	 */
	public static boolean isZipFile(File f) {

		boolean isZip = true;
		byte[] buffer = new byte[MAGIC.length];
		try {
			RandomAccessFile raf = new RandomAccessFile(f, "r");
			raf.readFully(buffer);
			for (int i = 0; i < MAGIC.length; i++) {
				if (buffer[i] != MAGIC[i]) {
					isZip = false;
					break;
				}
			}
			raf.close();
		} catch (Throwable e) {
			isZip = false;
		}
		return isZip;
	}

}
