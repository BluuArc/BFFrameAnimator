
/*
 * Created by: Joshua Castor
 * Started: January 29, 2016
 *   Note: the wiki version was merged with this on Feb 27, 2016
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

public class BFStripAnimator {
	public static void main(String[] args) throws Exception {
		System.out.println("Begin Program Execution of BFStripAnimator\n");

		// set variables
		boolean wiki = false;				//option for wiki
		boolean useArgs = false;			//option for use of command line arguments
		int wikiOption;						//option for wiki
		String[] unitIDs = new String[1];	//array of unit IDs
		String unitID;						//current unit ID
		String mainDir, dirFrame, dirGif;	//mainDir is directory of units sorted by unit ID
											//dirFrame is directory where frames will be saved
											//dirGif is directory  where GIFs will be saved
		String listFile = "";				//filepath of list.txt

		if (args.length != 0)
			useArgs = true;

		// if no command line parameters are given
		if (!useArgs) {
			//set wiki option
			wikiOption = SimpleInput
					.getIntNumber("What are you making? (0 for all, 1 for wiki, anything else to exit)");
			if(wikiOption != 0 && wikiOption != 1){
				System.out.println("Exiting application.");
				return;
			}
			wiki = BFFrameMaker.setOpacity(wikiOption);

			//output result (mostly for debugging purposes
			if (wiki) {
				System.out.println("Making idle GIFs for the wiki");
			} else {
				System.out.println("Making all animations");
			}

			// get unitIDs
			unitID = SimpleInput.getString("Enter the unit IDs separated by spaces. The wiki option is " + wiki);
			unitIDs = BFFrameMaker.parseString(unitID);
		} else if (args.length == 2) {
			// parse parameters
			/*
			 * sample input java BFFrameMaker [list.txt] [wiki] [list.txt] =
			 * file of unit IDs, one ID per line [wiki] = option fro wiki; 1 for
			 * true; 0 for false
			 */

			// set parameters
			listFile = args[0];
			wikiOption = Integer.parseInt(args[1]);

			// set wiki
			wiki = BFFrameMaker.setOpacity(wikiOption);

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
			wikiOption = Integer.parseInt(args[2]);
			wiki = BFFrameMaker.setOpacity(wikiOption);
		}
		
		//exit
		if(unitIDs[0].equals("-1")){
			System.out.println("Exiting application.");
			return;
		}

		System.out.println("Preparing to make " + unitIDs.length + " set(s) of GIFs...");

		// set directories
		if (!useArgs) {
			// manually set image directory
			System.out.println("Choose directory that contains the units sorted by IDs.");
			mainDir = FileChooser.pickAFile();
			BFFrameMaker.setDirectory(mainDir);
			mainDir = BFFrameMaker.getDirectory(mainDir);

			// set GIF directory
			System.out.println("Choosing target directory for GIFs...");
			dirGif = FileChooser.pickAFile();
			dirGif = BFFrameMaker.getDirectory(dirGif);
		} else {
			// use list file to set directories
			mainDir = BFFrameMaker.getDirectory(listFile) + "\\Units"; // units directory
			dirGif = BFFrameMaker.getDirectory(listFile) + "\\GIFs\\output";
		}
		dirFrame = dirGif + "\\frames";
		BFFrameMaker.setDirectory(dirFrame + "\\");
		System.out.println("Frames will be saved to " + dirFrame);

		// generate GIFs for each ID
		for (int u = 0; u < unitIDs.length; u++) {
			
			//set variables
			unitID = unitIDs[u];
			String workingDir = mainDir + "\\" + unitID;
			String workingFile = "";

			//debug("workingDir is [" + workingDir + "]");
			
			System.out.println("\n[Preparing to make GIFs for " + unitID + "]");
			
			//get files
			System.out.println("Getting CSV files");
			String[] cgsNames;
			if(!wiki)
				cgsNames = BFFrameMaker.getFiles(workingDir, "cgs", ".csv");	
			else
				cgsNames = BFFrameMaker.getFiles(workingDir, "idle_cgs", ".csv");	//wiki animations are only idle animations of units
			
			//check if getting files succeeded
			if(cgsNames == null){
				String message = "ERROR: ";
				
				if(cgsNames == null)
					message += "CGS file(s)";
				
				message += "is/are missing in [" + workingDir +"]";
				System.out.println(message);
				return;
			}
			
			//for each CGS file, create animation
			for(int c = 0; c < cgsNames.length; ++c){
				System.out.println("Parsing CGS File");
				workingFile = cgsNames[c];
				int [][] CGSFrames = new int[BFFrameMaker.getNumFrames(workingFile)][]; //CGS files contain frame order and delay for animation
				BFFrameMaker.parseCSV(workingFile, CGSFrames);
				
				Picture2[] GifFrames = new Picture2[1]; 	//array for current working set of frames
															//its length is the number of frames for that animation
				String type = getType(workingFile, true);
				
				//make animation
				if(CGSFrames.length != 0){
					System.out.println("\n[Making [" + type + "] GIF for " + unitID + "]");
					//extract frames
					GifFrames = extractFrames(BFFrameMaker.getFile(workingDir, getType(workingFile, false), ".png"), unitID, type, BFFrameMaker.getNumFrames(workingFile), wiki);
					
					//crop and save frames
					BFFrameMaker.makeNewFrame(GifFrames, unitID, CGSFrames, type);
					
					//make GIF from frames
					makeGif(dirGif, unitID, type, CGSFrames, wiki);
				}else{
					System.out.println("ERROR: File error with [" + workingFile + "]");
				}
				System.out.println("\n");
			}//end for each CGS file
			System.out.println("[Finished making GIFs for " + unitID + "]");
		} // end for each unit ID

		// reset directory
		BFFrameMaker.setDirectory(mainDir);

		System.out.println("Don't forget to clear out the frames folder.");

		System.out.println("\nEnd Program Execution of BFStripAnimator");
	} // end main method

	////////////////////////////// methods\\\\\\\\\\\\\\\\\\\\\\\\\\

	// method to initialize required pictures and files
	@Deprecated
	//deprecated because of creation of getFile and getFiles methods
	public static String[] setup(String dir, String unitID) {
		String[] fNames = new String[6]; // see below
		// System.out.println("Directory is " + dir);

		// declare files and pictures
		fNames[0] = dir + "\\" + unitID + "\\unit_idle_cgs_" + unitID + ".csv";
		fNames[1] = dir + "\\" + unitID + "\\unit_move_cgs_" + unitID + ".csv";
		fNames[2] = dir + "\\" + unitID + "\\unit_atk_cgs_" + unitID + ".csv";
		fNames[3] = dir + "\\" + unitID + "\\unit_idle_" + unitID + ".png";
		fNames[4] = dir + "\\" + unitID + "\\unit_move_" + unitID + ".png";
		fNames[5] = dir + "\\" + unitID + "\\unit_atk_" + unitID + ".png";

		return fNames;
	}// end setup method

	//method to get the filename of a given file path
	public static String getFilename(String fName) {
		int pos = fName.lastIndexOf(File.separatorChar);
		fName = fName.substring(pos + 1, fName.length());// becomes
															// unit_cgg_10011.csv
		return fName;
	}//end getFilename

	// method to get corresponding CSV file of sprite sheet
	@Deprecated
	//deprecated because of creation of getFile and getFiles methods
	public static String getCGSFile(String dir, String fName) { // ex inputs are
																// /some/dir and
																// /some/dir/unit_atk_10011.png
		int pos = fName.lastIndexOf(File.separatorChar);
		String unitID = BFFrameMaker.getUnitID(fName);
		String type = fName.substring(pos + 1, fName.length()); // fName becomes
																// unit_atk_10011.png
		pos = type.indexOf("_", 0); // get index of first underscore
		type = type.substring(pos + 1, type.indexOf("_", pos + 1));// becomes
																	// atk
		String cgsFile = dir + "\\unit_" + type + "_cgs_" + unitID + ".csv";
		return cgsFile;
	}// end getCGSFile()

	// method to get type of CGS file
	public static String getType(String fName, boolean name) { // ex input is
													// /some/dir/unit_atk_cgs_10273.csv
		int pos = fName.lastIndexOf(File.separatorChar);
		fName = fName.substring(pos + 1, fName.length()); // becomes
															// unit_atk_cgs_10273.csv
		pos = fName.indexOf("_", 0); // get location of first underscore from
										// index 0
		String type = fName.substring(pos + 1, fName.indexOf("cgs", pos + 1) - 1); // becomes
																					// atk
		
		//custom naming convention for easy sorting
		if(name){
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
		}
		
		return type;
	}// end getUnitID method

	// method to resize frames in array from given bounds
	@Deprecated
	//refer to BFFrameMaker.makeNewFrame instead
	public static void makeNewFrame(Picture2[] frame, String unitID, String type, int[][] csvFile) {
		int[] bounds = BFFrameMaker.getBounds(frame);
		int w = bounds[3] - bounds[2];
		int h = bounds[1] - bounds[0];

		for (int i = 0; i < frame.length; i++) {
			int delay = BFFrameMaker.FramesToMilliseconds(csvFile[i][3]);

			System.out.println("Cropping and saving frame number " + (i + 1) + "...");
			Picture2 part = new Picture2(w, h);
			BFFrameMaker.copyPicture(frame[i], bounds[2], bounds[0], part, 0, 0, w, h);
			// save frane as ./frame_<currentFrame>/part_<currentPart>.jpg
			// System.out.println("Saving frame number " + currentFrame +
			// "...");
			String fName = FileChooser.getMediaDirectory() + "\\unit_" + unitID + "_" + type + "-F" + i + "_" + delay
					+ ".png";
			part.write(fName);
		}

	}// end makeNewFrame method

	public static void makeNewFrameWiki(Picture2[] frame, String unitID, String type, int[][] csvFile) {
		for (int i = 0; i < frame.length; i++) {
			BFFrameMaker.printProgress("Cropping and saving frames. Status: ", BFFrameMaker.getPercent(i, frame.length));
			int delay = BFFrameMaker.FramesToMilliseconds(csvFile[i][3]);

			// save frame as ./unit_<unitID>_<type>-F<frameNumber>_<delay>.png
			String fName = FileChooser.getMediaDirectory() + "\\unit_" + unitID + "_" + type + "-F" + i + "_" + delay
					+ ".png";
			frame[i].write(fName);
			BFFrameMaker.printProgress("Cropping and saving frames. Status: ", BFFrameMaker.getPercent(i+1, frame.length));
		}//end for each frame
	}// end makeNewFrameWiki method

	// method to make a GIF
	public static void makeGif(String dirGif, String unitID, String type, int[][] csvFile, boolean wiki)
			throws Exception {
		String fName;
		if (wiki)
			fName = dirGif + "\\unit_ills_anime_" + unitID + "_strip.gif";
		else
			fName = dirGif + "\\unit_" + unitID + "_" + type + "_strip.gif";

		AnimatedGifEncoder g = new AnimatedGifEncoder();
		g.setQuality(1);
		g.setDispose(2);
		g.setTransparent(BFFrameMaker.transparentColor);
		g.setRepeat(0);
		g.start(fName);
		for (int i = 0; i < csvFile.length; i++) {
			BFFrameMaker.printProgress("Creating " + getFilename(fName) + ". Status: ",
					BFFrameMaker.getPercent(i, csvFile.length));
			int delay = BFFrameMaker.FramesToMilliseconds(csvFile[i][3]);
			BufferedImage currentFrame = ImageIO.read(new File(FileChooser.getMediaDirectory() + "\\unit_" + unitID
					+ "_" + type + "-F" + i + "_" + delay + ".png"));
			g.setDelay(delay);
			g.addFrame(currentFrame);
			BFFrameMaker.printProgress("Creating " + getFilename(fName) + ". Status: ",
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

	public static Picture2[] extractFrames(String sSheetName, String unitID, String type, int numFrames, boolean wiki) {
		//set variables
		Picture2 sSheet = new Picture2(sSheetName);
		int width = sSheet.getWidth() / numFrames;
		int height = sSheet.getHeight();
		int diff = 0;
		if ((height <= 140) && wiki) {
			diff = 140 - height;
		}
		
		//new array to output
		Picture2 newFrame[] = new Picture2[numFrames];
		
		//extract each frame
		for (int i = 0; i < numFrames; i++) {
			BFFrameMaker.printProgress("Extracting frames. Status: ", BFFrameMaker.getPercent(i, numFrames));
			// prepare template
			newFrame[i] = new Picture2(width, height + diff);
			newFrame[i].setAllPixelsToAColor(BFFrameMaker.transparentColor);
			newFrame[i].setAllPixelsToAnAlpha(255);

			// copy frame
			Pixel sourcePix;
			Pixel targetPix;
			int y = 0;

			//copy pixels
			for (int x = 0; x < width; x++) {
				for (y = 0; y < height; y++) {
					int sourceX = x + (width * i);
					int sourceY = y;
					sourcePix = sSheet.getPixel(sourceX, sourceY);
					targetPix = newFrame[i].getPixel(x, y + diff);

					int r, g, b;
					r = sourcePix.getRed();
					g = sourcePix.getGreen();
					b = sourcePix.getBlue();

					if (sourcePix.getAlpha() > 100) { 
						// copy pixel if there's something in the source pixel
						targetPix.setColor(new Color(r, g, b));
						targetPix.setAlpha(sourcePix.getAlpha());
					} // end if
				} // end for y
			} // end for x
			BFFrameMaker.printProgress("Extracting frames. Status: ", BFFrameMaker.getPercent(i + 1, numFrames));
		} // end for

		return newFrame;
	}// end extractFrame method

} // end of class