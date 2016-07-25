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
		//TODO: update these before releasing
		//TODO: look through all files for todos before uploading
		String versionNum = "v1.2.2";
		String updateDate =  "July 25, 2016";
		
		//header message
		String license = "Welcome to BFFrameAnimator.\n";
		license += "This program is licensed under the Creative Commons Attribution 3.0 United States License.\n";
		license += "Visit https://github.com/BluuArc/BFFrameAnimator for updates and information.\n";
		license += "This is version " + versionNum + ", which was last updated on " + updateDate;
		System.out.println(license);
		
		int input = 1;			//variable to store input number
		String[] arguments; 	// ID, list, opacity
		String menu, lastInput;	

		//create menu
		menu = "What would you like to do?\n";
		String[] menuOptions = {"Make all animations from spritesheets",
								"Make wiki GIFs from spritesheets",
								"Make GIFS from animation strips",
								"Create animation strips from spritesheets",
								"Exit"};
		
		//agenda (in some order)
		//TODO: create print title method -> System.out.println("[" + input + "]");
		//TODO: "properly" implement command line usage
		//TODO: make comments JDocs compatible
		//TODO: error output (ordering of missing?)
		/* example:
		 * The following had missing files
		 * 10011	atk, idle
		 * 10012	idle, move, atk
		 */
		//TODO: add option to make a specific animation (e.g. atk, idle, move, etc.)
		//TODO: improve rotation (see 201000105)
		//TODO: improve frame cropping (some issues with movement such as 860238)
		//TODO: add option to include a colored background
		//TODO: make (separately?) program to find yellow frames
		//TODO: create wiki?

		//get option for creating one/multiple units
		input = SimpleInput.getYesNoOption("Are you making more than 1 unit?");
		System.out.println(
				"Choose the list.txt file that contains all the unit IDs. Its directory is a baseline for all file operations");
		
		//making more than one unit
		if (input == 1) {
			arguments = new String[2]; // list, opacity

			// get file name of list.txt
			arguments[0] = FileChooser.pickAFile();
			arguments[1] = "no input";

			// while loop to make multiple versions of the same set of units
			//exit option is at end of array
			while (input != menuOptions.length) {
				lastInput = "\nYour last input was [" + input + " " + arguments[1] + "]";
				input = SimpleInput.getListOption(menu+lastInput, menuOptions)+1;
				executeCommand(input, arguments);
			} // end while menu loop
		} else {// if only making 1 unit
			input = 1;	//set to one temporarily for while loop
			arguments = new String[3]; // ID, list, opacity

			// get file name of list.txt
			arguments[1] = FileChooser.pickAFile();
			arguments[2] = "no input";

			//exit option is at end of array
			while (input != menuOptions.length) {
				lastInput = "\nYour last input was [" + input + " " + arguments[2] + "]";

				// get unitID
				arguments[0] = Integer.toString(SimpleInput.getIntNumber("What unit do you want to make?"));

				// get option
				input = SimpleInput.getListOption(menu+lastInput, menuOptions)+1;
				executeCommand(input, arguments);
			} // end while menu loop

		} // end else
		
		//ending message
		System.out.println("\nGoodbye.");
		
		System.exit(0);
	} // end main method

	// method to get opacity or wiki parameter for arguments
	public static String getOption(int option) {
		switch (option) {
		case 1: //opacity
			return Integer
					.toString(SimpleInput.getYesNoOption("Would you like to use opacity?"));
		case 2: //wiki
			String[] options = {"All", "Wiki"};
			return Integer.toString(SimpleInput.getButtonOption("What are you making?", options));
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