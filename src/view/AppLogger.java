package view;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class AppLogger {
	
	private static BufferedReader in;
	private static boolean debugMode = true;
	private static String debugFilename = "log.txt";
	
	
	static {
		in = new BufferedReader(new InputStreamReader(System.in));
	}
	
	public static void log(Object o) {
		if(debugMode) {
			try(PrintWriter out = new PrintWriter(new FileWriter(debugFilename,true))) {
				out.println(o);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(o);
		
	}
	
	public static String read(String message) {
		System.out.println(message);
		String result = null;
		try {
			result = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static int readInt(String message) {
		System.out.println(message);
		String result = null;
		try {
			result = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Integer.parseInt(result);
	}
	
	public static void logError(String message) {
		
	}
}
