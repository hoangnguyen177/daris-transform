package transform.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

	public static void delete(File f) throws IOException {
		if (f.isDirectory()) {
			for (File c : f.listFiles()) {
				delete(c);
			}
		}
		if (!f.delete()) {
			throw new IOException("Failed to delete file: " + f.getAbsolutePath());
		}
	}

	public static InputStream deleteOnCloseFileInputStream(File f) throws Throwable {
		return new DeleteOnCloseFileInputStream(f);
	}

	public static class DeleteOnCloseFileInputStream extends FileInputStream {

		private File _file;

		public DeleteOnCloseFileInputStream(File file) throws FileNotFoundException {
			super(file);
			_file = file;
		}

		public void close() throws IOException {
			super.close();
			_file.delete();
		}
	}
}
