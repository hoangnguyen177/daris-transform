package transform.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class CommandLineUtil {

	public static String[] parseCommandLine(String line) {
		StringTokenizer tokenizer = new StringTokenizer(line);
		List<String> tokens = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (token.startsWith("\"")) {
				while (tokenizer.hasMoreTokens()) {
					String token2 = tokenizer.nextToken();
					token += " " + token2;
					if (token2.endsWith("\"")) {
						break;
					}
				}
			}
			tokens.add(token);
		}
		String[] ts = new String[tokens.size()];
		tokens.toArray(ts);
		return ts;
	}
	
	public static void main(String[] args) {
//		String[] tokens = parseCommandLine("/bin/bash -c \"/bin/date +%Y\"");
		String[] tokens = parseCommandLine("/bin/date");
		for(int i=0;i<tokens.length;i++){
			System.out.println(tokens[i]);
		}
	}

}
