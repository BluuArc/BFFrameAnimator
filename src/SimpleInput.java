import javax.swing.JOptionPane;

/**
 * Class to make it easy to get input from a user using JOptionPane
 * 
 * Copyright Georgia Institute of Technology 2004
 * 
 * @author Barb Ericson ericson@cc.gatech.edu
 */
public class SimpleInput {
	/**
	 * Method to allow the user to input a number. the dialog will keep
	 * appearing till a valid number is input.
	 * 
	 * @param message
	 *            the message to display to the user in the dialog
	 * @return the number as a double
	 */
	public static double getNumber(String message) {
		boolean okay = true; // start out okay
		String answerString = null; // answer as a string
		double answer = 0;

		// Try to get a number using an input dialog
		do {
			// get the user's answer as a string
			answerString = JOptionPane.showInputDialog(message);

			// try to convert to a number
			try {
				answer = Double.parseDouble(answerString);
				okay = true;
			} catch (Exception ex) {
				/*
				 * not a number so set flag to false and change message
				 */
				okay = false;
				message = "Try again.  That wasn't a number!";
			}
		} while (!okay);

		// return the answer as a number
		return answer;
	}

	/**
	 * Method to allow the user to input an integer. The dialog will keep
	 * appearing till a valid number is input.
	 * 
	 * @param message
	 *            the message to display to the user in the dialog
	 * @return the number as an integer
	 */
	public static int getIntNumber(String message) {
		boolean okay = true; // start out okay
		String answerString = null; // answer as a string
		int answer = 0;

		// Try to get a number using an input dialog
		do {
			// get the user's answer as a string
			answerString = JOptionPane.showInputDialog(message);

			// try to convert to a number
			try {
				answer = Integer.parseInt(answerString);
				okay = true;
			} catch (Exception ex) {
				/*
				 * not a number so set flag to false and change message
				 */
				okay = false;
				message = "Try again.  That wasn't an integer!";
			}
		} while (!okay);

		// return the answer as a number
		return answer;
	}

	/**
	 * Method to get an integer between a minimum and maximum (inclusive)
	 * 
	 * @param message
	 *            the message to display to
	 * @param min
	 *            the minimum number
	 * @param max
	 *            the maximum number
	 * @return the user entered integer
	 */
	public static int getIntNumber(String message, int min, int max) {
		boolean okay = true; // start out okay
		String answerString = null; // answer as a string
		String failMessage = "Try again.  That wasn't an " + "integer between " + min + " and " + max + "!";
		int answer = 0;

		// Try to get a number using an input dialog
		do {
			// get the user's answer as a string
			answerString = JOptionPane.showInputDialog(message);

			// try to convert to a number
			try {
				answer = Integer.parseInt(answerString);

				/*
				 * check that the answer is in the allowed range
				 */
				if (answer >= min && answer <= max) {
					okay = true;
				} else {
					okay = false;
					message = failMessage;
				}
			} catch (Exception ex) {
				/*
				 * not a number so set flag to false and change message
				 */
				okay = false;
				message = failMessage;
			}
		} while (!okay);

		// return the answer as a number
		return answer;
	}

	/**
	 * Method to get the name of a directory
	 * 
	 * @param message
	 *            the message to display to the user
	 * @return the pathname for a directory
	 */
	public static String getDirectory(String message) {
		String name = getString(message);
		return name;
	}

	/**
	 * Method to get a string input by the user. The dialog will keep appearing
	 * till a string is entered.
	 * 
	 * @param message
	 *            the message to display to the user
	 * @return the input string
	 */
	public static String getString(String message) {
		boolean okay = true;
		String answer = null;

		do {
			answer = JOptionPane.showInputDialog(message);
			okay = true;

			// if null try again
			if (answer == null)
				okay = false;
		} while (!okay);

		// return the answer
		return answer;
	}
	
	/**
	 * Method to let the user choose from a list of options. The dialog will keep appearing
	 * till an option is chosen.
	 * 
	 * @param message
	 *            the message to display to the user
	 * @param options
	 * 			  the options to choose from
	 * @return the option chosen relative to the index of the option
	 */
	public static int getListOption(Object message, Object[] options) {
		boolean okay = false;
		String result = "";
		int answer = -1;
		do {
			result = (String)JOptionPane.showInputDialog(null, message, "SimpleInput", JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
			
			//get index of answer to return
			for(int i = 0; i < options.length; ++i){
				if(result.equals(options[i]))
					answer = i;
			}
			
			//loop if answer is not within range
			if(answer >= 0 && answer < options.length)
				okay = true;
		} while (!okay);

		// return the answer
		return answer;
	}
	
	/**
	 * Method to let the user click a button from a list of options. 
	 * 
	 * @param message
	 *            the message to display to the user
	 * @param options
	 * 			  the options to choose from
	 * @return the option chosen relative to the index of the option
	 */
	public static int getButtonOption(String message, Object[] options) {
		return JOptionPane.showOptionDialog(null, message, "SimpleInput", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,null,options,options[0]);
	}
	
	/**
	 * Method to get a Yes/No input by the user.
	 * 
	 * @param message
	 *            the message to display to the user
	 * @return a value of 0/false for No and 1/true for Yes
	 */
	public static boolean getYesNoOption(String message) {
		String[] options = {"No", "Yes"};
		return getButtonOption(message,options) == 1;
	}

} // end of SimpleInput class