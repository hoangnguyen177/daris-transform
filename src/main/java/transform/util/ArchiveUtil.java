package transform.util;

import java.util.List;

import arc.archive.ArchiveRegistry;
import arc.mime.MimeType;

public class ArchiveUtil {

	public static boolean isMimeTypeSupported(String name) {
		List<MimeType> ts = ArchiveRegistry.supportedMimeTypes();
		if (ts == null) {
			return false;
		}
		for (MimeType t : ts) {
			if (t.name().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public static MimeType getMimeTypeByName(String name) {
		List<MimeType> ts = ArchiveRegistry.supportedMimeTypes();
		if (ts == null) {
			return null;
		}
		for (MimeType t : ts) {
			if (t.name().equals(name)) {
				return t;
			}
		}
		return null;
	}
}
