package com.gmail.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
	
	private static final Pattern BASENAME = Pattern.compile(".*?([^/]*)$");
	
	public static void inputStreamToFile(StringBuffer s, String filePath) {
		FileOutputStream fo;
		try {
			fo = new FileOutputStream(filePath);
			fo.write(s.toString().getBytes());
			fo.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
	}
	
	public static String basename(String url) {
		Matcher matcher = BASENAME.matcher(url);
		if (matcher.matches()) {
			return matcher.group(1);
		} else {
			throw new IllegalArgumentException("Can't parse " + url);
		}
	}

}
