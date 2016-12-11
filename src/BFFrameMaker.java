/*
 *	Rewrite of BFFrameMaker project to better fit OOP principles.
 * 
 *	Class to make strips and animations of units.
 *
 *	Started 12/2/2016
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
import java.io.IOException;
import javax.imageio.ImageIO;

public class BFFrameMaker{
	private static boolean debugOutput;
	private static boolean saveParts;
	private String[] units;
	private Frame[] frames;

	public BFFrameMaker(){
		debugOutput = false;
		saveParts = false;
		units = null;
		frames = null;
	}
	
	public static void main(String[] args) {
		BFFrameMaker program = new BFFrameMaker();
		program.processCommands(args);
		program.test();

		//ask for input (via dialog?)
		// System.out.println("Choose list.txt that contains list of IDs.");
		// String list = FileChooser.pickAFile();
		// FileManagement.setDirectory(FileManager.getDirectory(list));

		// System.out.println("Pick directory for all units");
		// String unitDir = FileManagement.getDirectory(FileChooser.pickAFile());

		// System.out.println("Pick a directory for all GIFs");
		// String gifDir = FileManagement.getDirectory(FileChooser.pickAFile());

		// String[] listParsed = FileManagement.getLines(list);
		
		// for(String s : listParsed){
		// 	Unit unit = new Unit(s, gifDir, unitDir + "\\" + s);
		// 	boolean opac = false;
		// 	boolean saveParts = false;
		// 	boolean stripMode = false;
		// 	program.makeUnitAnim(unit, opac, saveParts, stripMode);

		// 	opac = true;
		// 	// saveParts = true;
		// 	stripMode = true;
		// 	program.makeUnitStrip(unit, opac, saveParts, stripMode);
		// 	program.makeUnitAnim(unit, opac, saveParts, stripMode);
		// }


		return;
	}

	private void processCommands(String[] args){
		//sample path input: "C:\\folder1\\folder2\\Units\\50017"
		for(int i = 0; i < args.length; ++i){
			if(args[i].equals("-debug"))	debugOutput = true;
			if(args[i].equals("-list"))		System.out.println(args[++i]);
			if(args[i].equals("-unitDir"))	System.out.println(args[++i]);
			if(args[i].equals("-gifDir"))	System.out.println(args[++i]);

		}
	}

	//used for debugging and development
	public void test(){
		String unitDir = "";
		try{
			unitDir = FileManagement.getDirectory(FileChooser.pickAFile());
			FileManagement.setDirectory(unitDir);
			// System.out.println(unitDir);
		}catch(NullPointerException e){
			System.out.println("Null input. Exiting.");
			System.exit(-1);
		}
		ProgramOutput.debug(debugOutput, "test: Creating unit");
		Unit unit = new Unit("860238", unitDir, unitDir);
		try{
			//unit = new Unit("50017", unitDir, unitDir);
			ProgramOutput.debug(debugOutput, "test: Checking validity of unit");
			if(!unit.isValidUnit()){
				System.out.println("Error in test: missing files for " + unit.getID());
				unit.addError("Error in test: missing files for " + unit.getID());
				// System.exit(-1);
			}

			boolean opac = false;
			boolean saveParts = false;
			boolean stripMode = false;
			// makeUnitAnim(unit, opac, saveParts, stripMode);

			opac = true;
			// saveParts = true;
			// stripMode = true;
			makeUnitStrip(unit, opac, saveParts);
			makeUnitAnim(unit, opac, saveParts, stripMode);

		}catch(NullPointerException e){
			System.out.println("NullPointerException encountered in animation process");
			unit.addError("NullPointerException encountered in animation process");
		}
		if(!unit.noErrors()){
			System.out.println("\nThe following errors were encountered:");
			unit.printErrors();
		}

		System.exit(0);
	}

	//common method used by makeUnitAnim and makeUnitStrip to initialize frames	
	private void initializeFrames(Unit unit, int[][] cggParsed, String[] cgs, String animType, boolean useOpacity, boolean makeParts){
		ProgramOutput.debug(debugOutput, "[entered initializeFrames]");
		//parse cgs file

		ProgramOutput.debug(debugOutput, "initializeFrames: parsing CGS for " + unit.getID());
		int[][] cgsParsed = new int[cgs.length][1];
		for(int i = 0; i < cgs.length; ++i){
			String[] temp = cgs[i].split(",");
			cgsParsed[i] = FileManagement.convertToInt(temp);
		}

		//initialize sprite sheet(s)
		ProgramOutput.debug(debugOutput, "initializeFrames: getting spritesheet(s) for " + unit.getID());
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

		//create frames
		frames = new Frame[cgs.length];
		Frame.resetDimensionsAndLP();
		ProgramOutput.printProgress("Generating frames for " + animType + " of " + unit.getID() + ". Status: ",0, frames.length);
		for(int i = 0; i < frames.length; ++i){
			// System.out.println("Frame " + cgsParsed[i][0]);
			frames[i] = new Frame(cggParsed,cgsParsed,i,sSheets, useOpacity, makeParts);
			ProgramOutput.printProgress(null,i+1, frames.length);
		}
		ProgramOutput.debug(debugOutput, "[left initializeFrames]");
	}

	//animate from spritesheet or a strip
	public void makeUnitAnim(Unit unit, boolean useOpacity, boolean saveParts, boolean stripMode){
		ProgramOutput.debug(debugOutput, "[entered makeUnitAnim]");
		String[] cgg = FileManagement.getLines(unit.getCGG());
		if(cgg == null){
			unit.addError("CGG file error");
		}
		ProgramOutput.debug(debugOutput, "makeUnitAnim: parsing CGG for " + unit.getID());
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
			String[] cgs = FileManagement.getLines(currCGS);
			if(cgs == null){
				unit.addError("CGS file error");
			}

			String animType = getType(currCGS, true);
			System.out.println("\n[" + unit.getID() + " - " + animType + " - GIF]");

			initializeFrames(unit,cggParsed,cgs,animType,useOpacity, !stripMode);// don't make parts if using strip

			//save parts
			String opacType = (useOpacity) ? "opac" : "nopac";
			if(!stripMode && saveParts){
				ProgramOutput.printProgress("Saving each frame as a strip for " + animType + " of " + unit.getID() + ". Status: ",0, frames.length);
				for(int i = 0; i < frames.length; ++i){
					frames[i].saveParts(unit.getDirUnit() + "\\unit_" + unit.getID() + "_" + animType + "_" + opacType + "_F" + i + "_parts.png");
					ProgramOutput.printProgress(null,i+1,frames.length);
				}
			}

			//generate GIF based on stripMode variable
			String stripPath = null;
			if(stripMode){ // look for strip
				ProgramOutput.debug(debugOutput, "makeUnitAnim: checking for stripPath");
				stripPath = FileManagement.getSpecificFile(unit.getDirUnit(), "unit_" + getType(animType,false) + "_" 
					+ unit.getID() + "_" + opacType, ".png");
				if(stripPath == null){ // no strip, note error and continue to next unit
					unit.addError("missing " + "unit_" + getType(animType,false) + "_" 
					+ unit.getID() + "_" + opacType + ".png");
					continue;
				}
			}
			makeGif(unit, animType, opacType, stripPath);
		}//end for each cgs
		ProgramOutput.debug(debugOutput, "[left makeUnitAnim]");
	}

	//create an animation strip
	public void makeUnitStrip(Unit unit, boolean useOpacity, boolean saveParts){
		ProgramOutput.debug(debugOutput, "[entered makeUnitStrip]");
		String[] cgg = FileManagement.getLines(unit.getCGG());
		if(cgg == null){
			unit.addError("CGG file error");
		}
		ProgramOutput.debug(debugOutput, "makeUnitStrip: parsing CGG for " + unit.getID());
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
			String[] cgs = FileManagement.getLines(currCGS);
			if(cgs == null){
				unit.addError("CGS file error");
			}

			String animType = getType(currCGS, false);
			System.out.println("\n[" + unit.getID() + " - " + animType + " - Strip]");

			initializeFrames(unit,cggParsed,cgs,animType,useOpacity, true); //always create parts
			
			//save parts
			String opacType = (useOpacity) ? "opac" : "nopac";
			if(saveParts){
				ProgramOutput.printProgress("Saving each frame as a strip for " + animType + " of " + unit.getID() + ". Status: ",0, frames.length);
				for(int i = 0; i < frames.length; ++i){
					frames[i].saveParts(unit.getDirUnit() + "\\unit_" + unit.getID() + "_" + animType + "_" + opacType + "_F" + i + "_parts.png");
					ProgramOutput.printProgress(null,i+1,frames.length);
				}
			}

			makeStrip(unit, animType, opacType);
		}
		ProgramOutput.debug(debugOutput, "[left makeUnitStrip]");
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

	private void makeAnimFrames(String preName, Color transparentColor, String stripPath){
		ProgramOutput.debug(debugOutput, "[entered makeAnimFrames]");
		Picture2 origFrame, animFrame;
		int limitingAlpha = 100;
		if(stripPath == null){
			ProgramOutput.debug(debugOutput, "makeAnimFrames: using frames for generation");
			for(int i = 0; i < frames.length; ++i){
				origFrame = frames[i].getImage();

				//copy frame onto new picture object with transparent color background
				animFrame = new Picture2(origFrame.getWidth(), origFrame.getHeight());
				animFrame.setAllPixelsToAColor(transparentColor);
				animFrame.setAllPixelsToAnAlpha(255);
				for(int y = 0; y < animFrame.getHeight(); ++y){
					for(int x = 0; x < animFrame.getWidth(); ++x){
						Pixel sourcePix = origFrame.getPixel(x,y);
						Pixel targetPix = animFrame.getPixel(x,y);
						if(sourcePix.getAlpha() > limitingAlpha){	
							targetPix.setColor(sourcePix.getColor());
							targetPix.setAlpha(sourcePix.getAlpha());
						}
					}
				}//end for every pixel

				//save new frame
				animFrame.write(preName + i + "_" + frames[i].getDelay() + ".png");
				frames[i].setFileName(preName + i + "_" + frames[i].getDelay() + ".png");
			}//end for every frame
		}else{//using strip as source
			ProgramOutput.debug(debugOutput, "makeAnimFrames: using strip for generation");
			origFrame = new Picture2(stripPath);
			int frameWidth = origFrame.getWidth() / frames.length;
			int frameHeight = origFrame.getHeight();
			//copy each part of strip into separate frames
			for(int i = 0; i < frames.length; ++i){
				animFrame = new Picture2(frameWidth, frameHeight);
				animFrame.setAllPixelsToAColor(transparentColor);
				animFrame.setAllPixelsToAnAlpha(255);
				for(int y = 0; y < frameHeight; ++y){
					for(int x = 0; x < frameWidth; ++x){
						Pixel sourcePix = origFrame.getPixel(frameWidth * i + x,y);
						Pixel targetPix = animFrame.getPixel(x,y);
						if(sourcePix.getAlpha() > limitingAlpha){	
							targetPix.setColor(sourcePix.getColor());
							targetPix.setAlpha(sourcePix.getAlpha());
						}
					}
				}//end for every pixel

				//save new frame
				animFrame.write(preName + i + "_" + frames[i].getDelay() + ".png");
				frames[i].setFileName(preName + i + "_" + frames[i].getDelay() + ".png");
			}

		}
		ProgramOutput.debug(debugOutput, "[left makeAnimFrames]");
	}

	//method to make a GIF from images
	private String makeGif(Unit u, String animType, String animOption, String stripPath){
		ProgramOutput.debug(debugOutput, "[entered makeGif]");
		String gifName = u.getDirGif() + "\\unit_" + u.getID() + "_" 
		+ animType + "_" + animOption;
		if(stripPath != null)	gifName += "_strip";
		gifName += ".gif";

		String frameName = u.getDirGif() + "\\unit_" + u.getID() + "_" 
		+ animType + "-F";

		//save all frames with transparent background
		// if(stripPath != null) System.out.println(stripPath);
		makeAnimFrames(frameName, u.getTransparentColor(), stripPath);

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
				System.out.println("Error in makeGif: Failed to access [" + currentFrame.toString() + "] for GIF");
				u.addError("Error in makeGif: Failed to access [" + currentFrame.toString() + "] for GIF");
			}
			g.setDelay(frames[i].getDelay());
			g.addFrame(currentFrame);
			ProgramOutput.printProgress(null, i+1, frames.length);
		}
		g.finish();

		//delete saved froames
		ProgramOutput.printProgress("Deleting old frames. Status: ", 0, frames.length);
		for(int i = 0; i < frames.length; ++i){
			File currentFrame =	currentFrame = new File(frames[i].getFilename());

			if(currentFrame == null || !currentFrame.delete()){
				System.out.println("Error in makeGif: Failed to delete [" + currentFrame.toString() + "]");
				u.addError("Error in makeGif: Failed to delete [" + currentFrame.toString() + "]");
			}
			ProgramOutput.printProgress(null, i+1, frames.length);
		}

		ProgramOutput.debug(debugOutput, "[left makeGif]");
		return gifName;
	}

	//method to create a strip from frames
	private String makeStrip(Unit u, String animType, String animOption){
		ProgramOutput.debug(debugOutput, "[entered makeStrip]");
		String stripName = u.getDirUnit() + "\\unit_" + getType(animType, false) + "_" 
		+ u.getID() + "_" + animOption + ".png";

		String frameName = u.getDirGif() + "\\unit_" + u.getID() + "_" 
		+ animType + "-F";

		//add each frame to strip
		int frameWidth = frames[0].getImage().getWidth();
		Picture2 strip = new Picture2(frameWidth*frames.length, frames[0].getImage().getHeight());
		ProgramOutput.printProgress("Creating " + FileManagement.getFilename(stripName) + ". Status: ", 0, frames.length);
		for(int i = 0; i < frames.length; ++i){
			strip.getGraphics().drawImage((BufferedImage) frames[i].getImage().getImage(), i * frameWidth, 0, null);
			ProgramOutput.printProgress(null, i+1, frames.length);	
		}
		strip.write(stripName);
		ProgramOutput.debug(debugOutput, "[left makeStrip]");
		return stripName;
	}
}