import java.io.FileNotFoundException;
import java.io.PrintWriter;

/*
 *	Rewrite of BFFrameMaker project to better fit OOP principles.
 * 
 *	Class to handle the program output to the console and log file.
 *
 *	Started 12/2/2016
 * 
 *	@author Joshua Castor
 */

public class ProgramOutput{
	public static String lastMessage;
	private static PrintWriter log = null;
	public static boolean debugMode = false;
	public static boolean isLogging = false;
	
	/*log methods*/
	public static void initLog(String filePath){
		try {
			log = new PrintWriter(filePath);
			isLogging = true;
		} catch (FileNotFoundException e) {
			System.out.println("Error in ProgramOutput.initLog: can't create/access " + filePath);
		}
	}
	
	public static void logException(Exception e){
		e.printStackTrace(log);
	}
	
	public static void logMessage(String s){
		if(isLogging) log.println(s);
	}
	
	public static void printLoggedMessage(boolean isDebugMessage, String s){
		if(isLogging) log.println(s);
		if(isDebugMessage)		ProgramOutput.debug(s);
		else					System.out.println(s);
	}
	
	public static void closeLog(){
		if(isLogging) log.close();
		isLogging = false;
	}
	
	/*debug methods*/
	public static void setDebugMode(boolean b){
		debugMode = b;
		if(debugMode)	System.out.println("Debug mode is on.");
		else			System.out.println("Debug mode is off.");
	}

	//add debug to beginning of a message
	public static void debug(String message){
		if(debugMode)
			System.out.println("DEBUG: " + message);
	}

	/*special output methods*/
	// print progress; assumes that you are on the line to edit in the console
	public static void printProgress(String preText, int cur, int total) {
		if(preText != null) lastMessage = preText;
		int percent = (int) (((double) (cur) / (double) total) * 100);
		if (percent <= 100)	System.out.printf("\r%s%03d%c", lastMessage, percent, '%');
		if (percent >= 100)	System.out.printf(" - Done\n");
	}// end printProgress


}