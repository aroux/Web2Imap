package com.gmail.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Utils {
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
}
