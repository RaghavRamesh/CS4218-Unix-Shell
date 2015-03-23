package sg.edu.nus.comp.cs4218.impl.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.GrepException;

public class GrepApp implements Application {

	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
		if (args == null) {
			throw new GrepException(Consts.Messages.ARG_NOT_NULL);
		}
		if (stdout == null) {
			throw new GrepException(Consts.Messages.OUT_STR_NOT_NULL);
		}
		if (args.length == 0) {
			throw new GrepException(Consts.Messages.NO_INP_FOUND);
		}

		Pattern pattern = null;
		try {
			pattern = Pattern.compile(args[0]);
		} catch (PatternSyntaxException e) {
			throw new GrepException(e.getMessage());
		}
		Matcher matcher = pattern.matcher("");

		BufferedReader reader = null;
		PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(stdout)));
		String line;

		if (args.length == 1) { // only REGEX string provided, take input from stdin
			if (stdin == null) {
				throw new GrepException(Consts.Messages.IN_STR_NOT_NULL);
			}
			reader = new BufferedReader(new InputStreamReader(stdin));

			try {
				while ((line = reader.readLine()) != null) {
					matcher.reset(line);

					if (matcher.find()) {
						writer.write(line);
						writer.write(System.getProperty("line.separator"));
					}
				}
				reader.close();
			} catch (IOException e) {
				throw new GrepException(e.getMessage());
			}

		} else { // the first argument will be REGEX string, the rest will be filenames
			for (int i = 1; i < args.length; i++) {
				String file = args[i];

				try {
					reader = new BufferedReader(new FileReader(file));
					while ((line = reader.readLine()) != null) {
						matcher.reset(line);

						if (matcher.find()) {
							writer.write(line);
							writer.write(System.getProperty("line.separator"));
						}
					}
					reader.close();

				} catch (IOException e) {
					throw new GrepException(e.getMessage());
				}

			}
		}

		writer.flush();
	}

}
