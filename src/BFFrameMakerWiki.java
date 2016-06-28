
/*
 * Created by: Joshua Castor
 * Started: December 23, 2015
 *   Based off of BFFrameMaker.java
 * Description: Using a CSV file, create frames from parameters on each line
 * 
 * important parts:  1 = part_count, 2,3 = x_pos,y_pos (relative to center of frame),
 *                   8,9 = img_x, img_y (top left corner of selection on spritesheet),
 * This program is licensed under the Creative Commons Attribution 3.0 United States License.
 * Visit https://github.com/BluuArc/BFFrameAnimator for updates.
 */

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class BFFrameMakerWiki {
	public static void main(String[] args) throws Exception {
		System.out.println("Begin Program Execution of BFFrameMakerWiki\n");

		// set variables
		boolean useOpacity = false;			//option for opacity
		boolean useArgs = false;			//option for use of command line arguments
		int opacOption;						//option for opacity
		String[] unitIDs = new String[1];	//array of unit IDs
		String unitID;						//current unit ID
		String dir, dirFrame, dirGif;		//dir is directory of units sorted by unit ID
											//dirFrame is directory where frames will be saved
											//dirGif is directory  where GIFs will be saved
		String listFile = "";				//filepath of list.txt

		if (args.length != 0)
			useArgs = true;

		// if no command line parameters are given
		if (!useArgs) {
			//set opacity option
			opacOption = SimpleInput
					.getIntNumber("Would you like to use opacity? (0 for no, 1 for yes, anything else to exit)");
			if(opacOption != 0 && opacOption != 1){
				System.out.println("Exiting application.");
				return;
			}
			useOpacity = BFFrameMaker.setOpacity(opacOption);

			//output result (mostly for debugging  purposes)
			if (useOpacity)
				System.out.println("Opacity option is on.\n");
			else
				System.out.println("Opacity option is off.\n");

			//get unitIDs
			unitID = SimpleInput.getString("Enter the unit IDs separated by spaces. The opacity option is " + useOpacity);
			unitIDs = BFFrameMaker.parseString(unitID);

		} else if (args.length == 2) {
			// parse parameters
			/*
			 * sample input java BFFrameMaker [list.txt] [opacity] [list.txt] =
			 * file of unit IDs, one ID per line [opacity] = option for opacity;
			 * 1 for true, 0 for false
			 */

			// set parameters
			listFile = args[0];
			opacOption = Integer.parseInt(args[1]);

			// set opacity
			useOpacity = BFFrameMaker.setOpacity(opacOption);

			// parse file into unitIDs array
			unitIDs = BFFrameMaker.parseList(listFile);
		} else if (args.length == 3) {
			// make 1 ID
			/*
			 * sample input java BFFrameMaker [ID] [list.txt] [opacity] [ID] =
			 * unit ID to make [list.txt] = file of unit IDs, one ID per line
			 * [opacity] = option for opacity; 1 for true, 0 for false
			 */
			
			// set parameters
			unitIDs[0] = args[0];
			listFile = args[1];
			opacOption = Integer.parseInt(args[2]);
			useOpacity = BFFrameMaker.setOpacity(opacOption);
		}
		
		//exit
		if(unitIDs[0].equals("-1")){
			System.out.println("Exiting application.");
			return;
		}

		System.out.println("Preparing to make " + unitIDs.length + " set(s) of GIFs...");

		//set directories
		if (!useArgs) {
			System.out.println("Choose directory that contains the units sorted by IDs.");
			dir = FileChooser.pickAFile();
			dir = BFFrameMaker.getDirectory(dir);

			// set GIF directory
			System.out.println("Choosing target directory for GIFs...");
			dirGif = FileChooser.pickAFile();
			dirGif = BFFrameMaker.getDirectory(dirGif);
		} else {
			// use list file to set directories
			dir = BFFrameMaker.getDirectory(listFile) + "\\Units"; // units directory
			dirGif = BFFrameMaker.getDirectory(listFile) + "\\GIFs\\output";
		}
		dirFrame = dirGif + "\\frames";
		BFFrameMaker.setDirectory(dirFrame + "\\");
		System.out.println("Frames will be saved to " + dirFrame);

		// begin creating sets of animations one by one
		for (int u = 0; u < unitIDs.length; u++) {
			
			//set variables
			unitID = unitIDs[u];
			String workingDir = dir + "\\" + unitID;
			String workingFile = "";
			
			//debug("workingDir is [" + workingDir + "]");
			
			System.out.println("\n[Preparing to make GIFs for " + unitID + "]");
			
			//get files
			System.out.println("Getting CSV files and sprite sheet");
			String[] cgsNames = BFFrameMaker.getFiles(workingDir, "idle_cgs", ".csv");				//wiki animations are only idle animations of units					
			Picture2 sSheet = new Picture2(BFFrameMaker.getFile(workingDir, "anime", ".png"));		//sprite sheet contains image data
			workingFile = BFFrameMaker.getFile(workingDir, "cgg", ".csv");
			
			//check if getting files succeeded
			//using getFile multiple times results in multiple error messages if file doesn't exist, but it's fine
			if(cgsNames == null || workingFile == null || BFFrameMaker.getFile(workingDir, "anime", ".png") == null){
				String message = "ERROR: ";
				
				if(cgsNames == null)
					message += "CGS file(s) and ";
				if(workingFile == null)
					message += "CGG file and ";
				if(BFFrameMaker.getFile(workingDir, "anime", ".png") == null)
					message += "sprite sheet ";
				
				message += "is/are missing in [" + workingDir +"]";
				System.out.println(message);
				return;
			}//end check
			
			//parse CGG file
			System.out.println("Parsing CGG File"); 					//CGG file contains position data for all animations 
			int[][] CGGFrames = new int[BFFrameMaker.getNumFrames(workingFile)][];	//reference: frames[frame number][parameters for frame]
			BFFrameMaker.parseCSV(workingFile, CGGFrames);


			//for each CGS file, create animation
			for(int c = 0; c < cgsNames.length; ++c){
				
				System.out.println("Parsing CGS File");
				workingFile = cgsNames[c];
				int [][] CGSFrames = new int[BFFrameMaker.getNumFrames(workingFile)][]; //CGS files contain frame order and delay for animation
				BFFrameMaker.parseCSV(workingFile, CGSFrames);
				
				Picture2[] GifFrames = new Picture2[1]; 	//array for current working set of frames
															//its length is the number of frames for that animation
				
				String type = BFStripAnimator.getType(workingFile);	//can be 1idle, 2move, 3atk, or original type like limit
				
				//make animation
				if(CGSFrames.length != 0){
					System.out.println("\n[Making [" + type + "] GIF for " + unitID + "]");
					GifFrames = new Picture2[CGSFrames.length];	//resize array to correct length of animation
					
					//make frames from sprite sheet
					for(int i = 0; i < CGSFrames.length; ++i){
						GifFrames[i] = BFFrameMaker.makeFrame(unitID, sSheet, CGGFrames, CGSFrames, i, useOpacity);
					}
					
					//crop and save frames 
					makeNewFrame(GifFrames, unitID, CGSFrames, type);
					
					//make GIF from frames
					makeGif(dirGif, unitID, type, CGSFrames, useOpacity);
				}else{
					System.out.println("ERROR: File error with [" + workingFile + "]");
				}
				System.out.println("\n");
			}//end for each CGS file
			System.out.println("[Finished making GIFs for " + unitID + "]");
		} // end for each unit ID

		// reset directory
		BFFrameMaker.setDirectory(dir);

		System.out.println("\nEnd Program Execution of BFFrameMaker");
	} // end main method

	////////////////////////////// methods\\\\\\\\\\\\\\\\\\\\\\\\\\

	// method to initialize required pictures and files
	@Deprecated 
	//deprecated because of creation of getFile and getFiles methods
	//may hold some relevance for creation of metal units 
	public static String[] setup(String dir, String id) {
		// check for special case
		int c = 0;
		int unitIDInt = Integer.parseInt(id);

		//metal units
		int[] ghost = { 10202, 20202, 30202, 40202, 50202, 60132 };
		int[] king = { 10203, 20203, 30203, 40203, 50203, 60133 };
		int[] god = { 10204, 20204, 30204, 40204, 50204, 60134 };
		for (c = 0; c < 5; c++) {
			if (unitIDInt == ghost[c]) {
				unitIDInt = ghost[5];
				c = 5;
			} else if (unitIDInt == king[c]) {
				unitIDInt = king[5];
				c = 5;
			} else if (unitIDInt == god[c]) {
				unitIDInt = god[5];
				c = 5;
			}
		} // end for

		String unitID = Integer.toString(unitIDInt);

		String[] fNames = new String[5];
		fNames[0] = dir + "\\" + unitID + "\\unit_cgg_" + unitID + ".csv";
		fNames[1] = dir + "\\" + unitID + "\\unit_idle_cgs_" + unitID + ".csv";
		fNames[2] = dir + "\\" + unitID + "\\unit_move_cgs_" + unitID + ".csv";
		fNames[3] = dir + "\\" + unitID + "\\unit_atk_cgs_" + unitID + ".csv";
		fNames[4] = dir + "\\" + unitID + "\\unit_anime_" + unitID + ".png";

		return fNames;
	}// end setup method

	// method to find upper coordinate where highest part ends
	public static int getUpperBound(Picture2[] part) {
		int upperCoord = 5000; // this will be the upper y coordinate

		for (int i = 0; i < part.length; i++) {
			Pixel p;

			int w = part[i].getWidth();
			int h = part[i].getHeight();

			int y = 0;
			for (int x = 0; x < w; x++) {
				for (y = 0; y < h; y++) {
					// get current pixels
					p = part[i].getPixel(x, y);
					Color c = p.getColor();
					// save y coordinate if it's lower than the previously saved coord
					// and if it's not the transparent color
					if ((y < upperCoord) && (Pixel.colorDistance(c, BFFrameMaker.transparentColor) != 0))
						upperCoord = y;
				} // end y
			} // end x
		} // end i
		

		if (upperCoord < 0)
			upperCoord = 0;

		return upperCoord;
	}// end getUpperBound method

	// method to find lowest coordinate where lowest part ends
	public static int getLowerBound(Picture2[] part) {
		int lowerCoord = 0; // this will be the lower y coordinate
		int maxH = part[0].getHeight();
		for (int i = 0; i < part.length; i++) {
			Pixel p;

			int w = part[i].getWidth();
			int h = part[i].getHeight();

			if (h > maxH)
				maxH = h;

			int y = 0;
			for (int x = 0; x < w; x++) {
				for (y = 0; y < h; y++) {
					// get current pixels
					p = part[i].getPixel(x, y);
					Color c = p.getColor();
					// save y coordinate if it's lower than the previously saved coord
					// and if it's not the transparent color
					if ((y > lowerCoord) && (Pixel.colorDistance(c, BFFrameMaker.transparentColor) != 0))
						lowerCoord = y;
				} // end y
			} // end x
		} // end i

		if (lowerCoord > maxH)
			lowerCoord = maxH;

		return lowerCoord;
	}// end getLowerBound method

	// method to find leftmost coordinate where leftmost part ends
	public static int getLeftBound(Picture2[] part) {
		int upperCoord = 5000; // this will be the leftmost y coordinate

		for (int i = 0; i < part.length; i++) {
			Pixel p;

			int w = part[i].getWidth();
			int h = part[i].getHeight();

			int x = 0;
			for (int y = 0; y < h; y++) {
				for (x = 0; x < w; x++) {
					// get current pixels
					p = part[i].getPixel(x, y);
					Color c = p.getColor();
					// save y coordinate if it's lower than the previously saved coord
					// and if it's not the transparent color
					if ((x < upperCoord) && (Pixel.colorDistance(c, BFFrameMaker.transparentColor) != 0))
						upperCoord = x;
				} // end y
			} // end x
		} // end i

		if (upperCoord < 0)
			upperCoord = 0;

		return upperCoord;
	}// end getLeftBound method

	// method to find rightmost coordinate where rightmost part ends
	public static int getRightBound(Picture2[] part) {
		int lowerCoord = 0; // this will be the lower y coordinate
		int maxW = part[0].getWidth();
		for (int i = 0; i < part.length; i++) {
			Pixel p;

			int w = part[i].getWidth();
			int h = part[i].getHeight();

			if (w > maxW)
				maxW = w;

			int x = 0;
			for (int y = 0; y < h; y++) {
				for (x = 0; x < w; x++) {
					// get current pixels
					p = part[i].getPixel(x, y);
					Color c = p.getColor();
					// save y coordinate if it's lower than the previously saved coord
					// and if it's not the transparent color
					if ((x > lowerCoord) && (Pixel.colorDistance(c, BFFrameMaker.transparentColor) != 0))
						lowerCoord = x;
				} // end y
			} // end x
		} // end i

		if (lowerCoord > maxW)
			lowerCoord = maxW;

		return lowerCoord;
	}// end getLowerBound method

	// method to get all bounds
	public static int[] getBounds(Picture2[] part) {
		int[] bounds = new int[4];

		BFFrameMaker.printProgress("Getting bounds. Status: ", 0);
		bounds[0] = getUpperBound(part);

		BFFrameMaker.printProgress("Getting bounds. Status: ", 25);
		bounds[1] = getLowerBound(part);

		BFFrameMaker.printProgress("Getting bounds. Status: ", 50);
		bounds[2] = getLeftBound(part);

		BFFrameMaker.printProgress("Getting bounds. Status: ", 75);
		bounds[3] = getRightBound(part);

		BFFrameMaker.printProgress("Getting bounds. Status: ", 100);

		return bounds;
	}// end getBounds method

	// method to resize frames in array from given bounds
	public static void makeNewFrame(Picture2[] frame, String unitID, int[][] csvFile, String type) {
		int[] bounds = getBounds(frame);
		int wF, hF; 	// frame dimensions
		int wNF, hNF;	// new frame dimensions
		int x, y;		// start coords on frame
		int sX, sY;		// start coords on new frame

		wF = bounds[3] - bounds[2];
		hF = bounds[1] - bounds[0];

		sX = 0;
		for (int i = 0; i < frame.length; i++) {
			int delay = BFFrameMaker.FramesToMilliseconds(csvFile[i][3]);

			BFFrameMaker.printProgress("Cropping and saving frames. Status: ",
					BFFrameMaker.getPercent(i, frame.length));

			//set x coord and width
			if (wF < 0) {
				wF = 250;
				x = 0;
			} else {
				x = bounds[2];
			}
			wNF = wF + 10;
			sX = 5;

			//set y coord and height
			if (hF > 140) {// if new height is bigger
				hNF = hF;
				sY = 0;
			} else {
				hNF = 140;
				sY = hNF - hF - 5;
				if (sY < 0)
					sY = 0;
			}
			y = bounds[0];

			//copy old part to new resized part
			Picture2 part = new Picture2(wNF, hNF);
			part.setAllPixelsToAColor(new Color(253, 237, 43));
			part.setAllPixelsToAnAlpha(255);
			copyPicture(frame[i], x, y, part, sX, sY, wNF, hNF);
			
			// save frame as ./unit_<unitID>_<type>-F<frameNumber>_<delay>.png
			String fName = FileChooser.getMediaDirectory() + "\\unit_" + unitID + "_" + type + "-F" + i + "_" + delay
					+ ".png";
			part.write(fName);
			BFFrameMaker.printProgress("Cropping and saving frames. Status: ", BFFrameMaker.getPercent(i + 1, frame.length));
		}//end for each frame
	}// end makeNewFrame method

	// method to make an exact copy of an image onto another image using
	// coordinates, no color required
	public static void copyPicture(Picture2 p, int sX1, int sY1, Picture2 p2, int sX2, int sY2, int eX2, int eY2) {
		// declare variables
		Pixel pix;
		int x;
		int y;
		Color pixColor;
		int pixAlpha;

		// for each row
		for (x = sX2; x < eX2; x++) {
			// for each column
			for (y = sY2; y < eY2; y++) {
				// store the color into a pixel in the result
				Pixel pix2;
				pix2 = p2.getPixel(x, y);

				// access the pixel on the first image at a specified coordinate
				// location
				int pX = sX1 + (x - sX2);
				int pY = sY1 + (y - sY2);
				if ((pX > (p.getWidth() - 1)) || (pY > (p.getHeight() - 1))) {
					pixColor = BFFrameMaker.transparentColor;
					pixAlpha = 255;
				} else {
					pix = p.getPixel(pX, pY);
					pixColor = pix.getColor();
					pixAlpha = pix.getAlpha();
				}

				// set pixel color in the final image
				pix2.setColor(pixColor);
				pix2.setAlpha(pixAlpha);
			} // end for y
		} // end for x
	}// end copyPicture method

	// method to make a GIF
	public static void makeGif(String dirGif, String unitID, String type, int[][] csvFile, boolean useOpacity)
			throws Exception {
		String fName = dirGif + "\\unit_ills_anime_" + unitID;
		if (!useOpacity)
			fName = fName + "_nopac";
		else
			fName = fName + "_opac";
		fName = fName + ".gif";
		
		//save gif as unit_ills_anime_<unitID>_<n/opac>.gif

		AnimatedGifEncoder g = new AnimatedGifEncoder();
		g.setQuality(1);
		g.setDispose(2);
		g.setTransparent(new Color(253, 237, 43));
		g.setRepeat(0);
		g.start(fName);
		for (int i = 0; i < csvFile.length; i++) {
			BFFrameMaker.printProgress("Creating " + BFStripAnimator.getFilename(fName) + ". Status: ",
					BFFrameMaker.getPercent(i, csvFile.length));
			int delay = BFFrameMaker.FramesToMilliseconds(csvFile[i][3]);
			BufferedImage currentFrame = ImageIO.read(new File(FileChooser.getMediaDirectory() + "\\unit_" + unitID
					+ "_" + type + "-F" + i + "_" + delay + ".png"));
			g.setDelay(delay);
			g.addFrame(currentFrame);
			BFFrameMaker.printProgress("Creating " + BFStripAnimator.getFilename(fName) + ". Status: ",
					BFFrameMaker.getPercent(i + 1, csvFile.length));
		}
		g.finish();
		
		//delete used frames
				for(int i = 0; i < csvFile.length; ++i){
					BFFrameMaker.printProgress("Deleting old frames. Status: ", BFFrameMaker.getPercent(i, csvFile.length));
					int delay = BFFrameMaker.FramesToMilliseconds(csvFile[i][3]);
					File currFrame = new File(FileChooser.getMediaDirectory() + "\\unit_" + unitID	+ "_" + type + "-F" + i + "_" + delay + ".png");
					if(!currFrame.delete()){
						System.out.println("ERROR: Failed to delete [" + currFrame.toString() + "]");
					}
					BFFrameMaker.printProgress("Deleting old frames. Status: ", BFFrameMaker.getPercent(i, csvFile.length));
				}
	}// end makeGif method

} // end of class