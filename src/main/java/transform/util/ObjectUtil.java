package transform.util;

public class ObjectUtil {

	public static boolean equals(Object a, Object b) {
		if (a == null) {
			if (b == null) {
				return true;
			}

			return false;
		}

		if (b == null) {
			return false;
		}

		return a.equals(b);
	}

}
