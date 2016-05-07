
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
		System.out.println("Begin Program Execution\n");

		boolean useOpacity = false;
		boolean useArgs = false;
		int opacOption;
		String unitID;
		String[] unitIDs = new String[1];
		String dir, dirFrame, dirGif;
		String listFile = "";

		if (args.length != 0)
			useArgs = true;

		// if no commandline parameters are given
		if (!useArgs) {

			opacOption = SimpleInput
					.getIntNumber("Would you like to use opacity? (0 for no, 1 for yes, anything else to exit)");
			useOpacity = BFFrameMaker.setOpacity(opacOption);

			if (useOpacity)
				System.out.println("Opacity option is on.\n");
			else
				System.out.println("Opacity option is off.\n");

			unitID = SimpleInput.getString("Enter the unit ID. The opacity option is " + useOpacity);
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

		System.out.println("Preparing to make " + unitIDs.length + " GIFs...");

		if (!useArgs) {
			System.out.println("Choose directory that contains the folders of 1 - 6.");
			dir = FileChooser.pickAFile();
			dir = BFFrameMaker.getDirectory(dir);

			// set frame directory
			// System.out.println("Choosing target directory for frames...");
			// dirFrame = FileChooser.pickAFile();
			// setDirectory(dirFrame);
			// dirFrame = getDirectory(dirFrame);

			// set GIF directory
			System.out.println("Choosing target directory for GIFs...");
			dirGif = FileChooser.pickAFile();
			dirGif = BFFrameMaker.getDirectory(dirGif);
		} else {
			// use list file to set directories
			dir = BFFrameMaker.getDirectory(listFile) + "\\Units"; // units
																	// directory
			dirGif = BFFrameMaker.getDirectory(listFile) + "\\GIFs\\output";
		}
		dirFrame = dirGif + "\\frames";
		BFFrameMaker.setDirectory(dirFrame + "\\");
		System.out.println("Frames will be saved to " + dirFrame);

		// for each ID
		for (int u = 0; u < unitIDs.length; u++) {

			unitID = unitIDs[u];

			String[] fNames = BFFrameMaker.setup(dir, unitID);
			Picture2 sSheet = new Picture2(fNames[4]);

			// create arrays
			int[] numFrames = new int[2]; // integer array to get number of
											// frames/lines of each CSV file
			for (int i = 0; i < numFrames.length; i++)
				numFrames[i] = BFFrameMaker.getNumFrames(fNames[i]);

			System.out.println("\n[Parsing CSV Files for " + unitID + "]");
			System.out.println("Parsing main CSV File");
			int[][] frames = new int[numFrames[0]][]; // second set of brackets
														// is for referencing
														// what's in the inner
														// array
			BFFrameMaker.parseCSV(fNames[0], frames);

			System.out.println("Parsing idle CSV File");
			int[][] idle = new int[numFrames[1]][];
			BFFrameMaker.parseCSV(fNames[1], idle);

			// make frames
			Picture2[] frame = new Picture2[1];

			// if(idle.length != 0){
			System.out.println("\n[Making idle GIF for " + unitID + "]");
			frame = new Picture2[idle.length];
			for (int i = 0; i < idle.length; i++) { // for each line
				frame[i] = BFFrameMaker.makeFrame(unitID, sSheet, frames, idle, i, useOpacity);
			}
			makeNewFrame(frame, unitID, idle, "1idle");
			// System.out.println("Making idle GIF for " + unitID + "...");
			makeGif(dirGif, unitID, "1idle", idle, useOpacity);
			// }else{
			// //error[0] = true;
			// System.out.println("No idle CSV file found for " + unitID + ".");
			// }
			System.out.println("\n");
		} // end for

		// reset directory
		BFFrameMaker.setDirectory(dir);

		System.out.println("Don't forget to clear out the frames folder.");

		System.out.println("\nEnd Program Execution");
	} // end main method

	////////////////////////////// methods\\\\\\\\\\\\\\\\\\\\\\\\\\

	// method to initialize required pictures and files
	public static String[] setup(String dir, String id) {
		// check for special case
		int c = 0;
		int unitIDInt = Integer.parseInt(id);

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
					// int a = p.getAlpha();
					Color c = p.getColor();
					// save y coordinate if it's lower than the previously saved
					// coord
					// and if there's something there (alpha !- 0)
					if ((y < upperCoord) && (Pixel.colorDistance(c, new Color(253, 237, 43)) != 0))// (a
																									// !=
																									// 0))
						upperCoord = y;
				} // end y
			} // end x
		} // end i
		/*
		 * if((upperCoord % 10) != 0) upperCoord = ((upperCoord - 9) / 10) * 10;
		 * else upperCoord = upperCoord - 10;
		 */

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
					// int a = p.getAlpha();
					Color c = p.getColor();
					// save y coordinate if it's lower than the previously saved
					// coord
					// and if there's something there (alpha !- 0)
					if ((y > lowerCoord) && (Pixel.colorDistance(c, new Color(253, 237, 43)) != 0))// (a
																									// !=
																									// 0))
						lowerCoord = y;
				} // end y
			} // end x
		} // end i
		/*
		 * if((lowerCoord % 10) != 0) lowerCoord = ((lowerCoord + 9) / 10) * 10;
		 * else lowerCoord = lowerCoord + 10;
		 */

		if (lowerCoord > maxH)
			lowerCoord = maxH;

		return lowerCoord;
	}// end getLowerBound method

	// method to find lefmost coordinate where leftmost part ends
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
					// int a = p.getAlpha();
					Color c = p.getColor();
					// save y coordinate if it's lower than the previously saved
					// coord
					// and if there's something there (alpha !- 0)
					if ((x < upperCoord) && (Pixel.colorDistance(c, new Color(253, 237, 43)) != 0))// (a
																									// !=
																									// 0))
						upperCoord = x;
				} // end y
			} // end x
		} // end i
		/*
		 * if((upperCoord % 10) != 0) upperCoord = ((upperCoord - 9) / 10) * 10;
		 * else upperCoord = upperCoord - 10;
		 */

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
					// int a = p.getAlpha();
					Color c = p.getColor();
					// save y coordinate if it's lower than the previously saved
					// coord
					// and if there's something there (alpha !- 0)
					if ((x > lowerCoord) && (Pixel.colorDistance(c, new Color(253, 237, 43)) != 0))// (a
																									// !=
																									// 0))
						lowerCoord = x;
				} // end y
			} // end x
		} // end i
		/*
		 * if((lowerCoord % 10) != 0) lowerCoord = ((lowerCoord + 9) / 10) * 10;
		 * else lowerCoord = lowerCoord + 10;
		 */

		if (lowerCoord > maxW)
			lowerCoord = maxW;

		return lowerCoord;
	}// end getLowerBound method

	// method to get all bounds
	public static int[] getBounds(Picture2[] part) {
		// System.out.println("Getting bounds...");
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
		int wF, hF; // frame dimensions
		int wNF, hNF;// new frame dimensions
		int x, y;// start coords on frame
		int sX, sY;// start coords on new frame

		wF = bounds[3] - bounds[2];
		hF = bounds[1] - bounds[0];
		// System.out.println("Height of sprite is " + hF);

		sX = 0;
		for (int i = 0; i < frame.length; i++) {
			// int currentFrame = csvFile[i][0];
			int delay = BFFrameMaker.FramesToMilliseconds(csvFile[i][3]);

			// System.out.println("Cropping and saving frame number " +
			// currentFrame + "...");
			BFFrameMaker.printProgress("Cropping and saving frames. Status: ",
					BFFrameMaker.getPercent(i, frame.length));
			/*
			 * if(bounds[3] < bounds[2]){ x = frame[i].getWidth() - bounds[3];
			 * //x is leftmost position wNF = frame[i].getWidth() -
			 * (frame[i].getWidth() - bounds[3]); }else{ x = bounds[2]; wNF =
			 * frame[i].getWidth() - (x * 2); }
			 */

			if (wF < 0) {
				wF = 250;
				x = 0;
			} else {
				x = bounds[2];
			}
			wNF = wF + 10;
			sX = 5;

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

			// System.out.println("Starting coordinates on old frame of
			// dimensions " + wF + "x" + hF + ": (" + x + "," + y + ")");

			// System.out.println("Starting coordinates on new frame of
			// dimensions " + wNF + "x" + hNF + ": (" + sX + "," + sY + ")");

			Picture2 part = new Picture2(wNF, hNF);
			part.setAllPixelsToAColor(new Color(253, 237, 43));
			part.setAllPixelsToAnAlpha(255);
			copyPicture(frame[i], x, y, part, sX, sY, wNF, hNF);
			// save frane as ./frame_<currentFrame>/part_<currentPart>.jpg
			// System.out.println("Saving frame number " + currentFrame +
			// "...");
			String fName = FileChooser.getMediaDirectory() + "\\unit_" + unitID + "_" + type + "-F" + i + "_" + delay
					+ ".png";
			part.write(fName);
			BFFrameMaker.printProgress("Cropping and saving frames. Status: ",
					BFFrameMaker.getPercent(i + 1, frame.length));
		}

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
					pixColor = new Color(253, 237, 43);
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

		AnimatedGifEncoder g = new AnimatedGifEncoder();
		g.setQuality(1);
		g.setDispose(2);
		g.setTransparent(new Color(253, 237, 43));
		g.setRepeat(0);
		g.start(fName);
		for (int i = 0; i < csvFile.length; i++) { // for each line
			// System.out.println("Adding frame " + (i + 1) + " of " +
			// csvFile.length + " to GIF of " + unitID);
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
	}// end makeGif method

} // end of class