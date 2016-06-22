/*
 * Created by: Joshua Castor
 * Description: Menu for the BF animation classes
 * This program is licensed under the Creative Commons Attribution 3.0 United States License.
 * Visit https://github.com/BluuArc/BFFrameAnimator for updates.
 */

// Note the name of the class in the following line MUST
// match the name of the file.  

public class BFAnimationMenu {
	public static void main(String[] args) {
		//TODO: update these before uploading to github
		String versionNum = "v1.1.1";
		String updateDate =  "June XX, 2016";
		
		//header message
		String license = "Welcome to BFFrameAnimator.\n";
		license += "This program is licensed unter the Creative Commons Attribution 3.0 United States License.\n";
		license += "Visit https://github.com/BluuArc/BFFrameAnimator for updates.\n";
		license += "This is version " + versionNum + ", which was last updated on " + updateDate;
		System.out.println(license);
		
		int input = 1;			//variable to store input number
		String[] arguments; 	// ID, list, opacity
		String menu, lastInput;	

		//create menu
		menu = "What would you like to do?\n";
		menu += " 1. Make all 3 animations from spritesheets\n";
		menu += " 2. Make wiki GIFs from spritesheets\n";
		menu += " 3. Make GIFS from animation strips\n";
		menu += " 4. Create animation strips from spritesheets\n";
		menu += "Enter 0 to exit.\n";

		//get option for creating one/multiple units
		input = SimpleInput.getIntNumber("Are you making more than 1 unit? (0 for no, 1 for yes)", 0, 1);
		System.out.println(
				"Choose the list.txt file that contains all the unit IDs. Its directory is a baseline for all file operations");
		
		//making more than one unit
		if (input == 1) {
			arguments = new String[2]; // list, opacity

			// get file name of list.txt
			arguments[0] = FileChooser.pickAFile();
			arguments[1] = "no input";

			// while loop to make multiple versions of the same set of units
			while (input != 0) {
				lastInput = "Your last input was [" + input + " " + arguments[1] + "]";
				input = SimpleInput.getIntNumber(menu + lastInput, 0, 4);
				executeCommand(input, arguments);
			} // end while menu loop
		} else {// if only making 1 unit
			input = 1;	//set to one temporarily for while loop
			arguments = new String[3]; // ID, list, opacity

			// get file name of list.txt
			arguments[1] = FileChooser.pickAFile();
			arguments[2] = "no input";

			while (input != 0) {
				lastInput = "Your last input was [" + input + " " + arguments[2] + "]";

				// get unitID
				arguments[0] = Integer.toString(SimpleInput.getIntNumber("What unit do you want to make?"));

				// get option
				input = SimpleInput.getIntNumber(menu + lastInput, 0, 4);
				executeCommand(input, arguments);
			} // end while menu loop

		} // end else
		
		//ending message
		System.out.println("\nGoodbye.");
		return;
	} // end main method

	// method to get opacity or wiki parameter for arguments
	public static String getOption(int option) {
		switch (option) {
		case 1: //opacity
			return Integer
					.toString(SimpleInput.getIntNumber("Would you like to use opacity? (0 for no, 1 for yes)", 0, 1));
		case 2: //wiki
			return Integer.toString(SimpleInput.getIntNumber("What are you making? (0 for all, 1 for wiki)", 0, 1));
		default:
			return "Error";
		}// end switch
	}// end getOption

	// method to execute commands, goes with the menu in main()
	public static void executeCommand(int input, String arguments[]) {
		String list; //save list directory for strip making option
		
		//more than one unit
		if (arguments.length == 2) {
			//indices for arguments: list, opacity
			list = arguments[0];
			switch (input) {
			case 1: //make all animations
				//opacity option
				arguments[1] = getOption(1);
				try {
					BFFrameMaker.main(arguments);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 2: //make only wiki animations 
				//opacity option
				arguments[1] = getOption(1);
				try {
					BFFrameMakerWiki.main(arguments);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 3: //animate strips
				//all or wiki option
				arguments[1] = getOption(2);
				try {
					BFStripAnimator.main(arguments);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 4: //make strips
				arguments = new String[1];
				arguments[0] = list;
				try {
					BFStripMaker.main(arguments);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
			}// end switch
		} else {// arguments.length == 3, only one unit
			// indices: ID, list, opacity
			String unitID = arguments[0];
			list = arguments[1];
			switch (input) {
			case 1: //make all animations
				arguments[2] = getOption(1);
				try {
					BFFrameMaker.main(arguments);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 2: //make wiki animations
				arguments[2] = getOption(1);
				try {
					BFFrameMakerWiki.main(arguments);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 3: //animate strips
				arguments[2] = getOption(2);
				try {
					BFStripAnimator.main(arguments);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 4: //make animation strips
				arguments = new String[2];
				arguments[0] = unitID;
				arguments[1] = list;
				try {
					BFStripMaker.main(arguments);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
			}// end switch
		}
	}// end executeCommand

} // end of class