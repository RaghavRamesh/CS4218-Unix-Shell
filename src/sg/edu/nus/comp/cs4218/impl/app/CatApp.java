package sg.edu.nus.comp.cs4218.impl.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CatException;
import sg.edu.nus.comp.cs4218.exception.InvalidDirectoryException;

public class CatApp implements Application {

	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout)
			throws AbstractApplicationException {

		if (args == null) {
			throw new CatException(Consts.Messages.ARG_NOT_NULL);
		}

		if (stdout == null) {
			throw new CatException(Consts.Messages.OUT_STR_NOT_NULL);
		}

		String requiredDirectory;
		BufferedReader reader = null;
		PrintWriter writer = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(stdout)));

		try {

			if (args.length == 0) {

				if (stdin == null) {
					throw new CatException(Consts.Messages.INP_STR_NOT_NULL);
				}

				reader = new BufferedReader(new InputStreamReader(stdin));
				String line = null;

				while ((line = reader.readLine()) != null) {
					writer.write(line);
				}

				writer.write("\n");
				return;
			}

			for (int i = 0; i < args.length; i++) {

				if (args[i].length() <= 0) {
					throw new CatException(Consts.Messages.FILE_NOT_VALID);
				}

				requiredDirectory = Environment.getCurrentDirectory()
						+ File.separator + args[i];
				File reqdPathAsFile = new File(requiredDirectory);

				boolean pathExists = reqdPathAsFile.exists();
				boolean pathIsFile = reqdPathAsFile.isFile();

				if (!pathExists) {
					throw new CatException("can't open '" + args[i] + "'. "
							+ Consts.Messages.FILE_NOT_FOUND);
				}

				if (!pathIsFile) {
					throw new CatException("can't open '" + args[i] + "'. "
							+ Consts.Messages.FILE_NOT_VALID);
				}

				if (pathExists && pathIsFile) {
					reader = new BufferedReader(new InputStreamReader(
							new FileInputStream(requiredDirectory)));
					String line = null;
					while ((line = reader.readLine()) != null) {
						writer.println(line);
					}
				}

				writer.flush();
				reader.close();
			}
		} catch (InvalidDirectoryException exception) {
			throw new CatException(exception);
		} catch (IOException exception) {
			throw new CatException(exception);
		}

	}

}
