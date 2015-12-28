package com.xiaohe.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class FileOperate {
	private static String defaultOutPutFile = "resource/outFile";
	/**
	 * @param filePath
	 * @throws Exception
	 * input a filePath then print the file content
	 */
	public static void readFile(String filePath) throws Exception {
		File inputfile = new File(filePath);
		FileReader fileReader = new FileReader(inputfile);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String strRead = null;
		while ((strRead = bufferedReader.readLine()) != null) {
			System.out.println(strRead);
		}
		bufferedReader.close();
	}
	public static void WriteFile(String line) throws Exception {
		WriteFile(defaultOutPutFile, line);
	}
	public static void WriteFile(String filePath, String line) throws Exception {
		File outputfile = new File(filePath);
		FileWriter fileWriter = new FileWriter(outputfile, true);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		bufferedWriter.write(line);
		bufferedWriter.write("\n");
		bufferedWriter.flush();
		bufferedWriter.close();
	}
	
	public static void main(String[] args) throws Exception {
		readFile("resource/fileReadTest");
		WriteFile("resource/outFile", "fileWriteTest");
	}
}
