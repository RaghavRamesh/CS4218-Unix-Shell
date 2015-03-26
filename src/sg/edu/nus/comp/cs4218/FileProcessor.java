package sg.edu.nus.comp.cs4218;

import java.io.BufferedReader;
import java.io.IOException;

import sg.edu.nus.comp.cs4218.exception.WcException;

public final class FileProcessor {

	private FileProcessor() {
	}

	public static int getByteCount(String fileContents) throws WcException {
		if (fileContents == null) {
			throw new WcException(Consts.Messages.CONTENTS_NOT_NULL);
		}

		return fileContents.length();
	}

	public static String readAndConvertToString(BufferedReader reader)
			throws IOException {
		StringBuilder builder = new StringBuilder();
		int currentChar = reader.read();
		while (currentChar != -1) {
			builder.append((char) currentChar);
			currentChar = reader.read();
		}

		reader.close();

		return builder.toString();
	}

	public static int getWordsLength(String fileContents) throws WcException {
		int count = 0;
		if (fileContents == null) {
			throw new WcException(Consts.Messages.CONTENTS_NOT_NULL);
		}

		if (fileContents.length() == 0)
			return 0;

		String[] tokenList = fileContents.trim().split("\\s+");
		for (String token : tokenList) {
			if (token.length() > 0) {
				count++;
			}
		}

		return count;
	}

	public static int getLineLength(String fileContents) throws WcException {
		int count = 0;
		if (fileContents == null) {
			throw new WcException(Consts.Messages.CONTENTS_NOT_NULL);
		}

		for (int i = 0; i < fileContents.length(); i++) {
			if (fileContents.charAt(i) == '\n') {
				count++;
			}
		}

		return count;
	}

}
