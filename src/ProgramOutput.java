/*
 *	Rewrite of BFFrameMaker project to better fit OOP principles.
 * 
 *	Class to handle the program output to the console.
 *
 *	Started 12/2/2016
 * 
 *	@author Joshua Castor
 */

public class ProgramOutput{
	public static String lastMessage;

	//add debug to beginning of a message
	public static void debug(boolean debugOutput, String message){
		if(debugOutput)
			System.out.println("DEBUG: " + message);
	}

	// print progress; assumes that you are on the line to edit in the console
	public static void printProgress(String preText, int cur, int total) {
		if(preText != null) lastMessage = preText;
		int percent = (int) (((double) (cur) / (double) total) * 100);
		if (percent <= 100)	System.out.printf("\r%s%03d%c", lastMessage, percent, '%');
		if (percent >= 100)	System.out.printf(" - Done\n");
	}// end printProgress


}