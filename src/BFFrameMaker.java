/*
 *	Rewrite of BFFrameMaker project to better fit OOP principles.
 * 
 *	Class to make strips and animations of units.
 *
 *	Started 12/2/2016
 * 
 *	This program is licensed under the Creative Commons Attribution 3.0 United States License.
 *	Visit https://github.com/BluuArc/BFFrameAnimator for updates.
 * 
 *	@author Joshua Castor
 */

/*
	rotation issues: 740216, 20017, 201000105
	opacity issues: Azurai, 740216, 11016, 11017

*/

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
//import java.io.FileNotFoundException;
import java.io.IOException;
//import java.io.PrintWriter;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class BFFrameMaker{
	private boolean useGUI;
	private String listPath;
	private String[] unitNums;
	private Frame[] frames;
	private CreationOptions options;
	
	private class CreationOptions{
		private boolean useOpacity;	//use opacity
		private boolean saveParts;	//save parts
		private boolean stripMode;	//use animation strip
		private boolean makeStrip;	//create animation strip
		private boolean wikiMode;	//create with height of 140px minimum
		private boolean shrinkMode; //option to remove bordering whitespace in an animation
		private String gifDir;		//dir of all gifs
		private String unitDir; 	//dir of all units
	
		public CreationOptions(){
			this(false, false, false, false, false, false, null, null);
		}
	
		public CreationOptions(boolean opac, boolean parts, boolean stripMo, boolean makeSt, boolean wikiMo, boolean shrinkMo, String gDir, String uDir){
			useOpacity = opac;
			saveParts = parts;
			stripMode = stripMo;
			makeStrip = makeSt;
			wikiMode = wikiMo;
			shrinkMode = shrinkMo;
			
			gifDir = gDir;
			unitDir = uDir;
		}
	
		public void setOpacity(boolean b){
			useOpacity = b;
		}
	
		public void setPartMode(boolean b){
			saveParts = b;
		}
	
		public void setStripMode(boolean b){
			stripMode = b;
		}
		
		public void setMakeStrip(boolean b){
			makeStrip = b;
		}
	
		public boolean isOpac(){
			return useOpacity;
		}
	
		public boolean doSaveParts(){
			return saveParts;
		}
	
		public boolean isStripMode(){
			return stripMode;
		}
		
		public boolean doMakeStrip(){
			return makeStrip;
		}
		
		public void setShrinkMode(boolean b){
			shrinkMode = b;
		}
		
		public boolean doShrink(){
			return shrinkMode;
		}
		
		public String toBooleanString(){
			String output = "[";
			output += (useOpacity) ? "useOpacity," : "!useOpacity,";
			output += (saveParts) ? "saveParts," : "!saveParts,";
			output += (stripMode) ? "stripMode," : "!stripMode,";
			output += (makeStrip) ? "makeStrip," : "!makeStrip,";
			output += (wikiMode) ? "wikiMode," : "!wikiMode,";
			output += (shrinkMode) ? "shrinkMode" : "!shrinkMode";
			output += "]";
			return output;
		}
		
		public void setGifDir(String d){
			if(d != null){
				gifDir = d;
			}else{
				System.out.println("Choose the destination directory for all GIFs.");
				gifDir = FileChooser.pickADirectory();
			}
			//System.out.println(gifDir);
		}
		
		public void setUnitDir(String d){
			if(d != null){
				unitDir = d;
			}else{
				System.out.println("Choose the parent directory for all units.");
				unitDir = FileChooser.pickADirectory();
			}
			//System.out.println(unitDir);
		}
		
		public String getGifDir(){
			return gifDir;
		}
		
		public String getUnitDir(){
			return unitDir;
		}
		
		public void setDirs(){
				setGifDir(gifDir);
				if(gifDir == null){
					ProgramOutput.printLoggedMessage(false, "Error in BFFrameMaker.setDirs: gifDir is null. Exiting.");
					ProgramOutput.closeLog();
					System.exit(-1);
				}
				
				setUnitDir(unitDir);
				if(unitDir == null){
					ProgramOutput.printLoggedMessage(false, "Error in BFFrameMaker.setDirs: unitDir is null. Exiting.");
					ProgramOutput.closeLog();
					System.exit(-1);
				}
		}
		
		public void setWikiMode(boolean b){
			wikiMode = b;
		}

		public boolean isWikiMode() {
			return wikiMode;
		}
	}

	/*constructors*/
	public BFFrameMaker(){
		unitNums = null;
		frames = null;
		options = null;
		useGUI = true;
	}
	
	/*methods*/
	public static void printStartupMessage(boolean toLog){
		String versionNum = "v2.1.0";
		String updateDate =  "December 25, 2016";
		
		//header message
		String license = "Welcome to BFFrameAnimator.\n";
		license += "This program is licensed under the Creative Commons Attribution 3.0 United States License.\n";
		license += "Visit https://github.com/BluuArc/BFFrameAnimator for updates and information.\n";
		license += "This is version " + versionNum + ", which was last updated on " + updateDate + "\n---\n";
		if(toLog) 	ProgramOutput.logMessage(license);
		else		System.out.println(license);
	}
	public static void main(String[] args) {
		BFFrameMaker.printStartupMessage(false);
		BFFrameMaker program = new BFFrameMaker();
		program.processCommands(args);
		boolean done = true; //set for 1 menu loop minimum

		//ask for input if necessary
		String list = program.getListPath();
		if(program.getNumUnits() == 0){
			try{
				System.out.println("Choose list.txt that contains a list of IDs to process.");
				list = FileChooser.pickAFile();
				program.setUnitsList(list);
			}catch(NullPointerException e){
				System.out.println("Error in BFFrameMaker.main: no valid input for list. Exiting.");
				ProgramOutput.logException(e);
				System.exit(-1);
			}
		}
		
		ProgramOutput.initLog(program.getGifDir() + "\\BFFA-log.txt");
		BFFrameMaker.printStartupMessage(true);
		
		ProgramOutput.debug("Log path: " + program.getGifDir() + "\\BFFA-log.txt");

		ProgramOutput.logMessage("gifDir: " + program.getGifDir());
		ProgramOutput.logMessage("unitDir: " + program.getUnitDir());
		ProgramOutput.logMessage("list: " + list + "\n");
		
		do{
			if(program.getGuiBool() == true)
				program.setAnimOptions();
	
			program.processUnits();
			
			if(program.getGuiBool() == true)
				done = !(SimpleInput.getYesNoOption("Would you like to generate another set of images and/or GIFs?"));
		}while(!done);
		ProgramOutput.closeLog();
		System.out.println("See [" + ProgramOutput.getLogPath() + "] if any errors occured.");
		System.exit(0);
	}
	
	//allow user to set animation options
	public void setAnimOptions(){
		boolean done = false;
		ProgramOutput.printLoggedMessage(true, "Old options: " + options.toBooleanString());
		while(!done){
			options.setMakeStrip(SimpleInput.getYesNoOption("Would you like to create animation strips?\n(Select No to create GIFs instead)"));
			if(!options.doMakeStrip()){
				options.setWikiMode(SimpleInput.getYesNoOption("Would you like to create wiki GIFs?\n(Select No to create all animations instead)"));
				options.setStripMode(SimpleInput.getYesNoOption("Would you like to use already made strips in your animations?"));
			}
			if(!options.isStripMode()) 	options.setOpacity(SimpleInput.getYesNoOption("Would you like to use opacity?"));
			else						options.setOpacity(SimpleInput.getYesNoOption("Do your strips use opacity?"));
			if(!options.isWikiMode() && !options.doMakeStrip())	options.setShrinkMode(SimpleInput.getYesNoOption("Would you like to remove the whitespace around animations (experimental)?"));
			if(!options.isStripMode())	options.setPartMode(SimpleInput.getYesNoOption("Would you like to save each frame as a part?")); //can't save individual parts from already made strips
			done = SimpleInput.getYesNoOption("Your options are currently set to be " + options.toBooleanString() + "\nAre you sure you want to continue?");
			if(!done){
				if(SimpleInput.getYesNoOption("Would you like to exit?")){
					ProgramOutput.closeLog();
					System.exit(0);
				}
			}
		}
		ProgramOutput.printLoggedMessage(true, "New options: " + options.toBooleanString());
	}
	
	public String getGifDir(){
		return (options.getGifDir());
	}
	
	public String getUnitDir(){
		return (options.getUnitDir());
	}
	
	public String getListPath(){
		return listPath;
	}
	
	//process command line input
	private void processCommands(String[] args){
		//sample path input: "C:\\folder1\\folder2\\Units\\50017"
		CreationOptions o = new CreationOptions();
		int i = 0;
		try{
			for(i = 0; i < args.length; ++i){
				if(args[i].equals("-unitDir"))		o.setUnitDir(args[++i]);
				if(args[i].equals("-gifDir"))		o.setGifDir(args[++i]);
				if(args[i].equals("-makeStrip"))	o.setMakeStrip(true);
				if(args[i].equals("-useStrip"))		o.setStripMode(true);
				if(args[i].equals("-useOpacity"))	o.setOpacity(true);
				if(args[i].equals("-saveParts"))	o.setPartMode(true);
				if(args[i].equals("-doShrink"))		o.setShrinkMode(true);
				if(args[i].equals("-noGui"))		useGUI = false;
				if(args[i].equals("-debug"))		ProgramOutput.setDebugMode(true);
				if(args[i].equals("-list")){
					listPath = args[++i];
					setUnitsList(listPath);
				}
				if(args[i].equals("-help")){
					String options = "";
					options += "Possible commandline options:\n";
					options += "-debug		output debug messages to console\n";
					options += "-unitDir <dir>	set the parent directory for all units\n";
					options += "-gifDir <dir>	set the destination directory for all GIFs\n";
					options += "-makeStrip	flag to create a strip of frames instead of a GIF per unit\n";
					options += "-useStrip	flag to use already made strips in the units folders\n";
					options += "-useOpacity	flag to use opacity creating directly from the spritesheet\n";
					options += "-saveParts	option to save each frame as a strip of parts in the units folders\n";
					options += "-list <path>	file path to a text file containing a list of units\n";
					options += "-noGui		flag to not use any GUI to set creation options\n";
					options += "-noGui(cont'd)	Note: if this flag is set, no GUI will be used assuming all other options are preset via command line arguments.\n"; 
					options += "-doShrink	remove most of the whitespace around animations; experimental feature\n"; 
					options += "-help		show this message\n";
					System.out.println(options);
					System.exit(0);
				}
			}
		}catch(ArrayIndexOutOfBoundsException e){
			System.out.println("Error in BFFrameMaker.processCommands: missing parameter for " + args[i-1]);
			ProgramOutput.logException(e);
		}
		
		o.setDirs(); //set directories if they aren't set yet
		options = o;
	}
	
	public boolean getGuiBool(){
		return useGUI;
	}
	
	public void setGuiBool(boolean b){
		useGUI = b;
	}
	
	public int getNumUnits(){
		if(unitNums != null)	return unitNums.length;
		else					return 0;
	}
	
	public void setUnitsList(String list){
		FileManagement.setDirectory(FileManagement.getDirectory(list));
		unitNums = FileManagement.getLines(list);
	}
	
	//create units array from array of unit IDs
	public Unit[] createUnitsArray(){
		ProgramOutput.printLoggedMessage(true, "[entered createUnitsArray]");
		ProgramOutput.printLoggedMessage(true, "createUnitsArray: checking that unitNum and options aren't null");
		assert(unitNums != null && options != null);
		ProgramOutput.printLoggedMessage(true, "createUnitsArray: converting String[] to Unit[] of size " + unitNums.length);
		Unit[] arr = new Unit[unitNums.length];
		try{
			for(int i = 0; i < unitNums.length; ++i){
				arr[i] = new Unit(unitNums[i], options.getGifDir(), options.getUnitDir() + "\\" + unitNums[i], options.isWikiMode());
			}
		}catch(NullPointerException e){
			ProgramOutput.printLoggedMessage(false, "Error in BFFrameMaker.createUnitsArray: NullPointerException encountered. Exiting.");
			ProgramOutput.logException(e);
			ProgramOutput.closeLog();
			System.exit(-1);
		}
		ProgramOutput.printLoggedMessage(true, "[left createUnitsArray]");
		return arr;
	}
	
	//process all units in current units array
	public void processUnits(){
		ProgramOutput.printLoggedMessage(true, "[entered processUnits]");
		if(getNumUnits() == 0){
			ProgramOutput.printLoggedMessage(false, "Error in BFFrameMaker.processUnits: List is empty.");
			ProgramOutput.printLoggedMessage(true, "[left processUnits]");
			return;
		}
		
		Unit[] unitArr = createUnitsArray();
			
		//process all units
		ProgramOutput.printLoggedMessage(true, "processUnits: processing each unit");
		for(Unit u : unitArr){
			processUnit(u);
		}
		
		//print error messages, if any
		ProgramOutput.printLoggedMessage(true, "processUnits: Printing error messages");
		ProgramOutput.printLoggedMessage(false, "\nThe following errors were encountered:");
		boolean error = false;
		for(Unit u : unitArr){
			if(!u.noErrors()){
				error = true;
				u.printErrors();
			}
		}
		if(!error) ProgramOutput.printLoggedMessage(false, "None.");
		ProgramOutput.printLoggedMessage(false, "\n");
		ProgramOutput.printLoggedMessage(true, "[left processUnits]");
	}
	
	//use options of current object
	public void processUnit(Unit unit){
		processUnit(unit, options);
	}
	
	//process an individual unit based on the options
	public void processUnit(Unit unit, CreationOptions o){
		ProgramOutput.printLoggedMessage(true, "[entered processUnit]");
		ProgramOutput.printLoggedMessage(true, "Current options are " + o.toBooleanString());
		try{
			ProgramOutput.printLoggedMessage(true, "processUnit: Checking validity of " + unit.getID());
			if(!unit.isValidUnit()){
				ProgramOutput.printLoggedMessage(false, "Error in BFFrameMaker.processUnit: missing files for " + unit.getID());
				unit.addError("Error in BFFrameMaker.processUnit: missing files for " + unit.getID());
				// System.exit(-1);
			}

			if(o.doMakeStrip()){//make a strip
				makeUnitStrip(unit, o.isOpac(), o.doSaveParts());
			}else{//make a gif
				makeUnitAnim(unit, o.isOpac(), o.doSaveParts(), o.isStripMode(), o.isWikiMode(), o.doShrink());
			}
			
		}catch(Exception e){
			ProgramOutput.printLoggedMessage(false, "Exception encountered in animation process with " + unit.getID());
			ProgramOutput.logException(e);
			unit.addError("Exception encountered in animation process");
		}
		if(!unit.noErrors()){
			unit.addError("Options for previous messages are " + o.toBooleanString());
		}
		ProgramOutput.printLoggedMessage(true, "[left processUnit]");
	}

	//common method used by makeUnitAnim and makeUnitStrip to initialize frames	
	private void initializeFrames(Unit unit, int[][] cggParsed, String[] cgs, String animType, boolean useOpacity, boolean makeParts, boolean saveParts){
		ProgramOutput.printLoggedMessage(true, "[entered initializeFrames]");
		
		//parse cgs file
		ProgramOutput.printLoggedMessage(true, "initializeFrames: parsing CGS for " + unit.getID());
		int[][] cgsParsed = new int[cgs.length][1];
		for(int i = 0; i < cgs.length; ++i){
			String[] temp = cgs[i].split(",");
			cgsParsed[i] = FileManagement.convertToInt(temp);
		}

		//initialize sprite sheet(s)
		ProgramOutput.printLoggedMessage(true, "initializeFrames: getting spritesheet(s) for " + unit.getID());
		Picture2[] sSheets = new Picture2[unit.getSheets().length];
		if(sSheets == null || sSheets.length == 0){
			unit.addError("spritesheet file error");
		}
		for(int i = 0; i < sSheets.length; ++i){
			sSheets[i] = new Picture2(unit.getSheets()[i]);
			if(sSheets[i] == null){
				unit.addError("spritesheet file error for " + i);
			}
		}

		//create and initialize frames
		if(frames != null){
			for(int i = 0; i < frames.length; ++i){
				frames[i] = null;
			}
			frames = null;
		}
		frames = new Frame[cgs.length];
		Frame.resetDimensionsAndLP();
		ProgramOutput.printProgress("Generating frames for " + animType + " of " + unit.getID() + ". Status: ",0, frames.length);
		String opacType = (useOpacity) ? "opac" : "nopac";
		for(int i = 0; i < frames.length; ++i){
			frames[i] = new Frame(cggParsed,cgsParsed,i,sSheets,unit.getDirUnit() + "\\unit_" + unit.getID() + "_" + animType + "_" + opacType + "_F" + i, useOpacity, makeParts, saveParts);
			ProgramOutput.printProgress(null,i+1, frames.length);
		}
		ProgramOutput.printLoggedMessage(true, "[left initializeFrames]");
	}

	//animate from spritesheet or a strip
	public void makeUnitAnim(Unit unit, boolean useOpacity, boolean saveParts, boolean stripMode, boolean wikiMode, boolean shrinkMode){
		ProgramOutput.printLoggedMessage(true, "[entered makeUnitAnim]");
		String[] cgg = FileManagement.getLines(unit.getCGG());
		if(cgg == null){
			unit.addError("CGG file error");
		}
		ProgramOutput.printLoggedMessage(true, "makeUnitAnim: parsing CGG for " + unit.getID());
		int[][] cggParsed = new int[cgg.length][1];
		for(int i = 0; i < cgg.length; ++i){
			String[] temp = cgg[i].split(",");
			cggParsed[i] = FileManagement.convertToInt(temp);
		}

		//make for every cgs
		String[] cgsFiles = unit.getCGS();
		for(int c = 0; c < cgsFiles.length; ++c){
			//read cgs file
			String currCGS = cgsFiles[c];
			try{
				String[] cgs = FileManagement.getLines(currCGS);
				if(cgs == null){
					unit.addError("CGS file error");
				}
	
				String animType = getType(currCGS, true);
				ProgramOutput.printLoggedMessage(false, "\n{" + unit.getID() + " - " + animType + " - GIF}");
	
				initializeFrames(unit,cggParsed,cgs,animType,useOpacity, !stripMode, saveParts);// don't make parts if using strip
	
				//save parts if necessary
				String opacType = (useOpacity) ? "opac" : "nopac";
				if(!stripMode && saveParts){
					ProgramOutput.printProgress("Saving each frame as a strip for " + animType + " of " + unit.getID() + ". Status: ",0, frames.length);
					for(int i = 0; i < frames.length; ++i){
						frames[i].saveParts(unit.getDirUnit() + "\\unit_" + unit.getID() + "_" + animType + "_" + opacType + "_F" + i + "_parts.png");
						ProgramOutput.printProgress(null,i+1,frames.length);
					}
					
					//delete parts since they aren't needed anymore
					for(Frame f : frames){
						f.deleteParts();
					}
				}
	
				//generate GIF based on stripMode variable
				String stripPath = null;
				if(stripMode){ // look for strip
					ProgramOutput.printLoggedMessage(true, "makeUnitAnim: looking for strip in the format of [unit_" + getType(animType,false) + "_" 
						+ unit.getID() + "_" + opacType + ".png]");
					stripPath = FileManagement.getSpecificFile(unit.getDirUnit(), "unit_" + getType(animType,false) + "_" 
						+ unit.getID() + "_" + opacType, ".png");
					if(stripPath == null){ // no strip, note error and continue to next unit
						unit.addError("Error in BFFrameMaker.makeUnitAnim: missing " + "unit_" + getType(animType,false) + "_" 
						+ unit.getID() + "_" + opacType + ".png");
						continue;
					}
				}
				makeGif(unit, wikiMode, animType, opacType, stripPath, shrinkMode);
			}catch(Exception e){
				ProgramOutput.printLoggedMessage(false, "Exception encountered in animation process with " + FileManagement.getFilename(currCGS));
				ProgramOutput.logException(e);
				unit.addError("Exception encountered in animation process with " + FileManagement.getFilename(currCGS));
			}
		}//end for each cgs
		ProgramOutput.printLoggedMessage(true, "[left makeUnitAnim]");
	}

	//create an animation strip
	public void makeUnitStrip(Unit unit, boolean useOpacity, boolean saveParts){
		ProgramOutput.printLoggedMessage(true, "[entered makeUnitStrip]");
		String[] cgg = FileManagement.getLines(unit.getCGG());
		if(cgg == null){
			unit.addError("CGG file error");
		}
		ProgramOutput.printLoggedMessage(true, "makeUnitStrip: parsing CGG for " + unit.getID());
		int[][] cggParsed = new int[cgg.length][1];
		for(int i = 0; i < cgg.length; ++i){
			String[] temp = cgg[i].split(",");
			cggParsed[i] = FileManagement.convertToInt(temp);
		}

		//make for every cgs
		String[] cgsFiles = unit.getCGS();
		for(int c = 0; c < cgsFiles.length; ++c){
			//read cgs file
			String currCGS = cgsFiles[c];
			try{
				String[] cgs = FileManagement.getLines(currCGS);
				if(cgs == null){
					unit.addError("CGS file error");
				}
	
				String animType = getType(currCGS, false);
				ProgramOutput.printLoggedMessage(false, "\n{" + unit.getID() + " - " + animType + " - Strip}");
	
				initializeFrames(unit,cggParsed,cgs,animType,useOpacity, true, saveParts); //always create parts
				
				//save parts
				String opacType = (useOpacity) ? "opac" : "nopac";
				if(saveParts){
					ProgramOutput.printProgress("Saving each frame as a strip for " + animType + " of " + unit.getID() + ". Status: ",0, frames.length);
					for(int i = 0; i < frames.length; ++i){
						frames[i].saveParts(unit.getDirUnit() + "\\unit_" + unit.getID() + "_" + animType + "_" + opacType + "_F" + i + "_parts.png");
						ProgramOutput.printProgress(null,i+1,frames.length);
					}
					
					//delete parts since they aren't needed anymore
					for(Frame f : frames){
						f.deleteParts();
					}
				}
				
	
				makeStrip(unit, animType, opacType);
			}catch(Exception e){
				ProgramOutput.printLoggedMessage(false, "Exception encountered in animation process with " + FileManagement.getFilename(currCGS));
				ProgramOutput.logException(e);
				unit.addError("Exception encountered in animation process with " + FileManagement.getFilename(currCGS));
			}
		}
		ProgramOutput.printLoggedMessage(true, "[left makeUnitStrip]");
	}

	//example filePath: path//to//unit_atk_cgs_10011.csv or unit_atk_10011.png
	//output should be atk or 3atk based on boolean
	private String getType(String filePath, boolean specialName){
		String type = FileManagement.getFilename(filePath);
		try{
			type = type.split("_")[1];
		}catch(ArrayIndexOutOfBoundsException e){
			//do nothing since it could already be given as one of the name types
		}

		//custom naming convention for easy sorting
		if(specialName){
			switch (type) {
				case "idle":
					type = "1idle";
					break;
				case "move":
					type = "2move";
					break;
				case "atk":
					type = "3atk";
					break;
				default:
					break;
			}
		}else{// convert back to traditional naming convention
			switch(type){
				case "1idle":
					type = "idle";
					break;
				case "2move":
					type = "move";
					break;
				case "3atk":
					type = "atk";
					break;
				default:
					break;
			}
		}
		
		return type;
	}

	private void makeAnimFrames(Unit u, String preName, Color transparentColor, String stripPath, boolean wikiMode, boolean shrinkMode){
		ProgramOutput.printLoggedMessage(true, "[entered makeAnimFrames]");
		Picture2 origFrame, animFrame;
		int limitingAlpha = 100;
		int startYSource = 0;
		int startYTarget = 0;
		int startXSource = 0;
		int startXTarget = 0;
		if(stripPath == null){
			ProgramOutput.printLoggedMessage(true, "makeAnimFrames: using frames for generation");
			
			int height = frames[0].getImage().getHeight();
			int width = frames[0].getImage().getWidth();
			if(wikiMode || shrinkMode){//resize for wikiMode
				int[] points = Frame.getLowestHighestFramePoints(frames);
				int newHeight = points[1] - points[0]; //get "true" height
				if(wikiMode){
					if(newHeight < 140){
						startYTarget = 140 - newHeight - 1;
						newHeight = 140;	//minimum height is 140 px
					}
					else				u.addError("Warning in makeAnimFrames: newHeight is " + newHeight);
				}
				startYSource = points[0];
				height = newHeight;
				
				startXSource = points[2];
				width = points[3] - points[2];
				
				//round width to next ten
				int mod = width % 10;
				if(mod != 0){
					startXTarget = (10 - mod) / 2;
					width += 10 - mod;
				}else{
					startXTarget = 5;
					width += 10;
				}
			}
			
			ProgramOutput.printProgress("Saving frames for GIF creation. Status: ", 0, frames.length);
			for(int i = 0; i < frames.length; ++i){
				origFrame = frames[i].getImage();

				//copy frame onto new picture object with transparent color background
				animFrame = new Picture2(width, height);
				animFrame.setAllPixelsToAColor(transparentColor);
				animFrame.setAllPixelsToAnAlpha(255);
				for(int y = 0; y < height; ++y){
					for(int x = 0; x < width; ++x){
						try{
							Pixel sourcePix = origFrame.getPixel(startXSource + x,startYSource + y);
							Pixel targetPix = animFrame.getPixel(startXTarget + x,startYTarget + y);
							if(sourcePix.getAlpha() > limitingAlpha){	
								targetPix.setColor(sourcePix.getColor());
								targetPix.setAlpha(sourcePix.getAlpha());
							}
						}catch(ArrayIndexOutOfBoundsException e){
							continue;
						}
					}
				}//end for every pixel

				//save new frame
				animFrame.write(preName + i + "_" + frames[i].getDelay() + ".png");
				frames[i].setFileName(preName + i + "_" + frames[i].getDelay() + ".png");
				ProgramOutput.logMessage("makeAnimFrames: created " + frames[i].getFilename());
				ProgramOutput.printProgress(null, i+1, frames.length);
			}//end for every frame
		}else{//using strip as source
			ProgramOutput.printLoggedMessage(true, "makeAnimFrames: using strip for generation");
			origFrame = new Picture2(stripPath);
			int frameWidth = origFrame.getWidth() / frames.length;
			int frameHeight = origFrame.getHeight();
			int newWidth = frameWidth;
			if(wikiMode || shrinkMode){//resize for wikiMode
				int[] points = Frame.getLowestHighestFramePoints(frames, origFrame);
				int newHeight = points[1] - points[0]; //get "true" height
				if(wikiMode){
					if(newHeight < 140){
						startYTarget = 140 - newHeight - 1;
						newHeight = 140;	//minimum height is 140 px
					}
					else				u.addError("Warning in makeAnimFrames: newHeight is " + newHeight);
				}
				startYSource = points[0];
				frameHeight = newHeight;
				
				startXSource = points[2];
				newWidth = points[3] - points[2];
				
				//round newWidth to next ten
				int mod = newWidth % 10;
				if(mod != 0){
					startXTarget = (10 - mod) / 2;
					newWidth += (10 - mod);
				}else{
					startXTarget = 5;
					newWidth += 10;
				}
			}
			//ProgramOutput.logMessage("BFFrameMaker.makeAnimFrames: newWidth is " + newWidth + " and frameWidth is " + frameWidth);
			
			//copy each part of strip into separate frames
			ProgramOutput.printProgress("Separating frames from strip for GIF creation. Status: ", 0, frames.length);
			for(int i = 0; i < frames.length; ++i){
				animFrame = new Picture2(newWidth, frameHeight);
				animFrame.setAllPixelsToAColor(transparentColor);
				animFrame.setAllPixelsToAnAlpha(255);
				int startX = (i * frameWidth) + startXSource;
				for(int y = 0; y < frameHeight; ++y){
					for(int x = 0; x < newWidth; ++x){
						try{
							Pixel sourcePix = origFrame.getPixel(startX + x,startYSource + y);
							Pixel targetPix = animFrame.getPixel(startXTarget + x,startYTarget + y);
							if(sourcePix.getAlpha() > limitingAlpha){	
								targetPix.setColor(sourcePix.getColor());
								targetPix.setAlpha(sourcePix.getAlpha());
							}
						}catch(ArrayIndexOutOfBoundsException e){
							continue;
						}
					}
				}//end for every pixel

				//save new frame
				animFrame.write(preName + i + "_" + frames[i].getDelay() + ".png");
				frames[i].setFileName(preName + i + "_" + frames[i].getDelay() + ".png");
				ProgramOutput.logMessage("Frame.save: created " + frames[i].getFilename());
				ProgramOutput.printProgress(null, i+1, frames.length);
			}

		}
		ProgramOutput.printLoggedMessage(true, "[left makeAnimFrames]");
	}

	//method to make a GIF from images
	@SuppressWarnings("static-access")
	private String makeGif(Unit u, boolean wikiMode, String animType, String animOption, String stripPath, boolean shrinkMode){
		ProgramOutput.printLoggedMessage(true, "[entered makeGif]");
		String gifName = u.getDirGif();
		if(!wikiMode)	gifName += "\\unit_" + u.getID() + "_" + animType + "_" + animOption;
		else			gifName += "\\unit_ills_anime_" + u.getID() + "_" + animOption;
		if(stripPath != null)	gifName += "_strip";
		if(shrinkMode)	gifName += "_shrink";
		gifName += ".gif";

		String frameName = u.getDirGif() + "\\unit_" + u.getID() + "_" 
		+ animType + "-F";

		//save all frames with transparent background
		makeAnimFrames(u, frameName, u.getTransparentColor(), stripPath, wikiMode, shrinkMode);

		//create gif from saved frames
		AnimatedGifEncoder g = new AnimatedGifEncoder();
		g.setQuality(1);	//highest quality for colors
		g.setDispose(2);	//animation scheme to be replace
		g.setTransparent(u.getTransparentColor()); //set transparent color
		g.setRepeat(0);		//gif will replay indefinitely
		g.start(gifName);	//begin making gif
		//add each frame to gif
		ProgramOutput.printProgress("Creating " + FileManagement.getFilename(gifName) + ". Status: ",0, frames.length);
		for(int i = 0; i < frames.length; ++i){
			BufferedImage currentFrame = null;
			try{
				currentFrame = ImageIO.read(new File(frames[i].getFilename()));
			}catch(IOException e){
				ProgramOutput.printLoggedMessage(false, "Error in BFFrameMaker.makeGif: Failed to access [" + frames[i].getFilename() + "] for GIF");
				ProgramOutput.logException(e);
				u.addError("Error in BFFrameMaker.makeGif: Failed to access [" + frames[i].getFilename() + "] for GIF");
			}
			g.setDelay(frames[i].getDelay());
			g.addFrame(currentFrame);
			ProgramOutput.printProgress(null, i+1, frames.length);
		}
		g.finish();
		ProgramOutput.logMessage("makeGif: created " + gifName);

		//delete saved frames
		ProgramOutput.printProgress("Deleting old frames. Status: ", 0, frames.length);
		for(int i = 0; i < frames.length; ++i){
			File currentFrame = new File(frames[i].getFilename());

			if(currentFrame == null || !currentFrame.delete()){
				ProgramOutput.printLoggedMessage(false, "Error in BFFrameMaker.makeGif: Failed to delete [" + currentFrame.toString() + "]");
				u.addError("Error in BFFrameMaker.makeGif: Failed to delete [" + currentFrame.toString() + "]");
			}else{
				ProgramOutput.logMessage("makeGif: deleted " + frames[i].getFilename());
			}
			ProgramOutput.printProgress(null, i+1, frames.length);
		}
		
		//check for yellow frames
		//based off of: http://stackoverflow.com/questions/8933893/convert-each-animated-gif-frame-to-a-separate-bufferedimage/32119229#32119229
		try {
			//open gif for analyzing
		    ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
		    File input = new File(gifName);
		    ImageInputStream stream = ImageIO.createImageInputStream(input);
		    reader.setInput(stream);

		    int count = reader.getNumImages(true);
		    int numImproperFrames = 0;
		    ProgramOutput.printProgress("Checking " + FileManagement.getFilename(gifName) + " for improper frames. Status: ",0,count);
		    //count the number of frames that aren't properly transparent in gif
		    for (int index = 0; index < count; index++) {
		        Picture2 currFrame = new Picture2(reader.read(index));
		        int x = currFrame.getWidth() - 1;
		        int y = currFrame.getHeight() - 1;
		        int countImproper = 0;
		        
		        for(int i = 0; i <= 1; ++i){
		        	for(int j = 0; j <= 1; ++j){
		        		Pixel sourcePix = currFrame.getPixel(x*i, y*j);
		        		if(sourcePix.colorDistance(u.getTransparentColor()) < 10 && sourcePix.getAlpha() > 0){
		        			countImproper++;
		        		}
		        	}
		        }
		        //if(countImproper != 0) currFrame.write(FileManagement.getDirectory(gifName) + "\\" + animType + "-F" + index + ".png");
		        
		        if(countImproper == 4)
		        	numImproperFrames++; //all 4 corners have transparent color
		        
		        ProgramOutput.printProgress(null,index+1,count);
		    }
		    if(numImproperFrames > 0){//there is at least one "improper" frame in gif
		    	u.addError("Warning in makeGif: There " + ((numImproperFrames > 1) ? "are " : "is ") + numImproperFrames + " improper " 
		    			+ ((numImproperFrames > 1) ? "frames " : "frame ") + "present in " + FileManagement.getFilename(gifName));
		    }
		} catch (IOException e) {
			ProgramOutput.printLoggedMessage(false, "Error in BFFrameMaker.makeGif: cannot access " + gifName + " for error checking.");
			ProgramOutput.logException(e);
		    u.addError("Error in BFFrameMaker.makeGif: cannot access " + gifName + " for error checking.");
		}

		ProgramOutput.printLoggedMessage(true, "[left makeGif]");
		return gifName;
	}

	//method to create a strip from frames
	private String makeStrip(Unit u, String animType, String animOption){
		ProgramOutput.printLoggedMessage(true, "[entered makeStrip]");
		String stripName = u.getDirUnit() + "\\unit_" + getType(animType, false) + "_" 
		+ u.getID() + "_" + animOption + ".png";

		//add each frame to strip
		int frameWidth = frames[0].getImage().getWidth();
		Picture2 strip = new Picture2(frameWidth*frames.length, frames[0].getImage().getHeight());
		ProgramOutput.printProgress("Creating " + FileManagement.getFilename(stripName) + ". Status: ", 0, frames.length);
		for(int i = 0; i < frames.length; ++i){
			strip.getGraphics().drawImage((BufferedImage) frames[i].getImage().getImage(), i * frameWidth, 0, null);
			ProgramOutput.printProgress(null, i+1, frames.length);	
		}
		strip.write(stripName);
		ProgramOutput.logMessage("makeStrip: created " + stripName);
		ProgramOutput.printLoggedMessage(true, "[left makeStrip]");
		return stripName;
	}
}
