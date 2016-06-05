import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;

/*
 * Created by: Joshua Castor
 * Started: June 6, 2015
 * Description: Using a CSV file, create one large frame file from parameters on each line
 * 
 * important parts:  1 = part_count, 2,3 = x_pos,y_pos (relative to center of frame),
 *                   8,9 = img_x, img_y (top left corner of selection on spritesheet),
 * 
 * This program is licensed under the Creative Commons Attribution 3.0 United States License.
 * Visit https://github.com/BluuArc/BFFrameAnimator for updates.
 */

public class BFStripMaker {

	public static void main(String[] args) {
		boolean useArgs = false;
		String[] unitIDs = new String[1];
		String unitID;
		String dir, dirFrame, dirGif;
		String listFile = "";
		

		// if no command line parameters are given
		if (!useArgs) {
			unitID = SimpleInput.getString("Enter the unit ID.");
			unitIDs = BFFrameMaker.parseString(unitID);

		} else if (args.length == 1) {
			// parse parameters
			/*
			 * sample input java BFFrameMaker [list.txt] 
			 * [list.txt] = file of unit IDs, one ID per line 
			 */

			// set parameters
			listFile = args[0];

			// parse file into unitIDs array
			unitIDs = BFFrameMaker.parseList(listFile);
		} else if (args.length == 2) {
			// make 1 ID
			/*
			 * sample input java BFFrameMaker [ID] [list.txt] 
			 * [ID] = unit ID to make 
			 * [list.txt] = file of unit IDs, one ID per line
			 */
			// set parameters
			unitIDs[0] = args[0];
			listFile = args[1];
		}
			
		// set directory
		System.out.println("Preparing to make " + unitIDs.length + " animation strips...");
		
		if (!useArgs) {
			System.out.println("Choose directory that contains the units sorted by IDs.");
			dir = FileChooser.pickAFile();
			dir = BFFrameMaker.getDirectory(dir);

			// set frame directory
			// System.out.println("Choosing target directory for frames...");
			// dirFrame = FileChooser.pickAFile();
			// setDirectory(dirFrame);
			// dirFrame = getDirectory(dirFrame);

			// set GIF directory
			System.out.println("Choosing target directory for GIFs...");
			// System.out.println(" Frames will be in GIFDirectory\frames");
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
		

		// for each ID
		for (int u = 0; u < unitIDs.length; u++) {

			unitID = unitIDs[u];

			String[] fNames = BFFrameMaker.setup(dir, unitID);
			Picture2 sSheet = new Picture2(fNames[4]);

			// create arrays
			int[] numFrames = new int[4]; // integer array to get number of
											// frames/lines of each CSV file
			for (int i = 0; i < numFrames.length; i++)
				numFrames[i] = BFFrameMaker.getNumFrames(fNames[i]);

			System.out.println("\n[Parsing CSV Files for " + unitID + "]");
			System.out.println("Parsing main CSV File");
			int[][] frames = new int[numFrames[0]][]; // reference: frames[frame number][parameters for frame]
			BFFrameMaker.parseCSV(fNames[0], frames);

			System.out.println("Parsing idle CSV File");
			int[][] idle = new int[numFrames[1]][];
			BFFrameMaker.parseCSV(fNames[1], idle);

			System.out.println("Parsing move CSV File");
			int[][] move = new int[numFrames[2]][];
			BFFrameMaker.parseCSV(fNames[2], move);

			System.out.println("Parsing attack CSV File");
			int[][] atk = new int[numFrames[3]][];
			BFFrameMaker.parseCSV(fNames[3], atk);

			// make frames
			Picture2[] frame = new Picture2[1];

			if (idle.length != 0) {
				System.out.println("\n[Making idle GIF for " + unitID + "]");
				frame = new Picture2[idle.length];
				// System.out.printf("Copying initial frames. Status: ");
				for (int i = 0; i < idle.length; i++) { // for each line
					frame[i] = makeFrame(unitID, sSheet, frames, idle, i);
				}
				makeNewFrame(frame, unitID, idle, "1idle");
				// System.out.printf("Making idle GIF for " + unitID + "...");
				makeStrip(dirGif, unitID, "1idle", idle);
			} else {
				// error[0] = true;
				System.out.println("No idle CSV file found for " + unitID + ".");
			}
			System.out.println("\n");
			if (move.length != 0) {
				System.out.println("[Making movement GIF for " + unitID + "]");
				frame = new Picture2[move.length];
				for (int i = 0; i < move.length; i++) { // for each line
					frame[i] = makeFrame(unitID, sSheet, frames, move, i);
				}
				makeNewFrame(frame, unitID, move, "2move");
				// System.out.println("Making movement GIF for " + unitID +
				// "...");
				makeStrip(dirGif, unitID, "2move", move);
			} else {
				// error[1] = true;
				System.out.println("No movement CSV file found for " + unitID + ".");
			}
			System.out.println("\n");
			if (atk.length != 0) {
				System.out.println("[Making attack GIF for " + unitID + "]");
				frame = new Picture2[atk.length];
				for (int i = 0; i < atk.length; i++) { // for each line
					frame[i] = makeFrame(unitID, sSheet, frames, atk, i);
				}
				makeNewFrame(frame, unitID, atk, "3atk");
				// System.out.println("Making attack GIF for " + unitID +
				// "...");
				makeStrip(dirGif, unitID, "3atk", atk);
			} else {
				// error[2] = true;
				System.out.println("No attack CSV file found for " + unitID + ".");
			}
			System.out.println("\n");
		} // end for

		// reset directory
		BFFrameMaker.setDirectory(dir);

		System.out.println("Don't forget to clear out the frames folder in " + dirFrame);

	}
	
	// method to make a frame from a spritesheet and save it to the right
	// directory
	public static Picture2 makeFrame(String unitID, Picture2 sSheet, int[][] frames, int[][] csvFile, int counter) {
		int currentFrame = csvFile[counter][0];
		int[] frame = frames[currentFrame];
		int numParts = frame[1];

		// create general template for all frames
		int width = BFFrameMaker.getLargestWidth(frames, csvFile);
		int height = BFFrameMaker.getLargestHeight(frames, csvFile);
		Picture2 part = new Picture2(width, height);
		//part.setAllPixelsToAColor(new Color(253, 237, 43));
		part.setAllPixelsToAnAlpha(0);

		BFFrameMaker.printProgress("Copying framees from spritesheet. Status: ", BFFrameMaker.getPercent(counter, csvFile.length));

		// System.out.println("Dimensions of frame (WxH): " + width + "x" +
		// height);

		// System.out.println("Copying " + numParts + " parts onto frame number
		// " + currentFrame);

		for (int f = 0; f < numParts; f++) {
			// System.out.println("Copying part number " + (numParts - 1 - f) +
			// " onto frame number " + currentFrame);
			part = copyPart(sSheet, part, frame, (numParts - 1 - f));
		}

		BFFrameMaker.printProgress("Copying framees from spritesheet. Status: ", BFFrameMaker.getPercent(counter + 1, csvFile.length));

		return part;
	}// end makeFrame method
	
	@SuppressWarnings("unused")
	public static Picture2 copyPart(Picture2 sSheet, Picture2 part, int[] frame, int currentPart) {
		int frameX = frame[2 + (currentPart * 11)];
		int frameY = frame[3 + (currentPart * 11)];
		int flip = frame[4 + (currentPart * 11)];
		int blendMode = frame[5 + (currentPart * 11)];
		int opac = frame[6 + (currentPart * 11)];
		int rotate = frame[7 + (currentPart * 11)];
		int spriteX = frame[8 + (currentPart * 11)];
		int spriteY = frame[9 + (currentPart * 11)];
		int w = frame[10 + (currentPart * 11)];
		int h = frame[11 + (currentPart * 11)];
		
		double opacity = opac / 100.0;
		if (rotate < 0)
			rotate = rotate + 360;
		double angle = Math.toRadians((double) (rotate));
		double sin = Math.sin(angle);
		double cos = Math.cos(angle);
		double x0 = 0.5 * (w - 1); // point to rotate about
		double y0 = 0.5 * (h - 1); // center of part
		int r0, c0;
	    int r1, c1;
	    int rows, cols;
	    rows = w;
	    cols = h;
	    
	    double rads = (rotate * 3.14159265)/180.0; //converts the degree given by user into radians
	    //find midpoints
	    r0 = rows / 2;
	    c0 = cols / 2;
	    
	    Picture2 tempPart = new Picture2(part.getWidth(),part.getHeight());
		Pixel sourcePix;
		Pixel targetPix;
		int offsetX = (part.getWidth() / 2) + frameX;
		int offsetY = (part.getHeight() / 2) + frameY;

		// System.out.println("Starting coordinates on frame are (" + offsetX +
		// "," + offsetY + ")");

		int y = 0;
		for (int x = 0; x < w; x++) {
			for (y = 0; y < h; y++) {
				// get current pixel position
				int sourceX = spriteX + x;
				int sourceY = spriteY + y;
				if (sourceX >= sSheet.getWidth())
					sourceX = sSheet.getWidth() - 1;
				if (sourceY >= sSheet.getHeight())
					sourceY = sSheet.getHeight() - 1;
				
				//get current source pixel
				sourcePix = sSheet.getPixel(sourceX, sourceY);
				
				//set target pixels
				int targetX;
				int targetY;
				
				//set flip
				if (flip == 1 || flip == 3)
					targetX = offsetX + (w - 1 - x);
				else
					targetX = offsetX + x;

				if (flip == 2 || flip == 3)
					targetY = offsetY + (h - 1 - y);
				else
					targetY = offsetY + y;
				
				//TODO rotation

				// System.out.println("Coordinates are (" + offsetX + "," +
				// offsetY + ")");

				targetPix = tempPart.getPixel(targetX, targetY);

				int r, g, b, a;
				r = sourcePix.getRed();
				g = sourcePix.getGreen();
				b = sourcePix.getBlue();
				a = sourcePix.getAlpha();

				if((blendMode == 1) && (a != 0) && opacity > 0 && a > 0){
					int pixval = (r + g + b) / 3;
					targetPix.setColor(new Color(r, g, b));
					if(targetPix.getAlpha() == 0)
						targetPix.setAlpha(pixval);
				}else if(opacity > 0 && a > 0){
					targetPix.setColor(new Color(r, g, b));
					if(targetPix.getAlpha() == 0 || a > targetPix.getAlpha())
						targetPix.setAlpha(a);
				}
				
			} // end y
		} // end x
		
		part.getGraphics().drawImage(tempPart.getImage(), 0, 0, null);

		return part;
	}// end copyPart method
	
	// method to resize frames in array from given bounds
	public static void makeNewFrame(Picture2[] frame, String unitID, int[][] csvFile, String type) {
		int[] bounds = getBounds(frame);
		int w = bounds[3] - bounds[2];
		int h = bounds[1] - bounds[0];

		for (int i = 0; i < frame.length; i++) {
			// int currentFrame = csvFile[i][0];
			//int delay = BFFrameMaker.FramesToMilliseconds(csvFile[i][3]);

			BFFrameMaker.printProgress("Cropping and saving frames. Status: ", BFFrameMaker.getPercent(i, frame.length));
			// System.out.println("Cropping and saving frame number " +
			// currentFrame + "...");
			Picture2 part = new Picture2(w, h);
			BFFrameMaker.copyPicture(frame[i], bounds[2], bounds[0], part, 0, 0, w, h);
			// save frame as ./frame_<currentFrame>/part_<currentPart>.jpg
			// System.out.println("Saving frame number " + currentFrame +
			// "...");
			String fName = FileChooser.getMediaDirectory() + "\\unit_" + unitID + "_" + type + "-F" + i	+ ".png";
			part.write(fName);
			BFFrameMaker.printProgress("Cropping and saving frames. Status: ", BFFrameMaker.getPercent(i + 1, frame.length));
		}
	}// end makeNewFrame method
	

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
					if ((y < upperCoord)&& (p.getAlpha()!= 0))															// 0))
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
					// int a = p.getAlpha();
					Color c = p.getColor();
					// save y coordinate if it's lower than the previously saved
					// coord
					// and if there's something there (alpha !- 0)
					if ((y > lowerCoord)&& (p.getAlpha()!= 0))															// 0))
						lowerCoord = y;
				} // end y
			} // end x
		} // end i

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
					if ((x < upperCoord)&& (p.getAlpha()!= 0))															// 0))
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
					// int a = p.getAlpha();
					Color c = p.getColor();
					// save y coordinate if it's lower than the previously saved
					// coord
					// and if there's something there (alpha !- 0)
					if ((x > lowerCoord) && (p.getAlpha()!= 0))
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
		// System.out.printf("Getting bounds. Status: ");
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




	// method to make an animation strip
	public static void makeStrip(String dirGif, String unitID, String type, int[][] csvFile){
		String fName = dirGif + "\\unit_" + unitID + "_" + type;
		fName = fName + ".png";
		System.out.println("Save Path:" + fName);
	
		//make blank strip
		Picture2 currentFrame = new Picture2(FileChooser.getMediaDirectory() + "\\unit_" + unitID+ "_" + type + "-F0.png");
		Picture2 strip = new Picture2(currentFrame.getWidth() * csvFile.length, currentFrame.getHeight());
		
		//copy frames onto strip
		for (int i = 0; i < csvFile.length; i++) { // for each line
			// System.out.println("Adding frame " + (i + 1) + " of " +
			// csvFile.length + " to GIF of " + unitID);
			BFFrameMaker.printProgress("Creating " + BFStripAnimator.getFilename(fName) + ". Status: ",
					BFFrameMaker.getPercent(i, csvFile.length));
			
			currentFrame = new Picture2(FileChooser.getMediaDirectory() + "\\unit_" + unitID+ "_" + type + "-F" + i + ".png");
			BFFrameMaker.copyPicture(currentFrame, 0, 0, strip, currentFrame.getWidth() * i, 0, currentFrame.getWidth() * (i + 1), currentFrame.getHeight());
			
			BFFrameMaker.printProgress("Creating " + BFStripAnimator.getFilename(fName) + ". Status: ",
					BFFrameMaker.getPercent(i + 1, csvFile.length));
		}
		
		strip.write(fName);
		BFFrameMaker.printProgress("Creating " + BFStripAnimator.getFilename(fName) + ". Status: ",
				BFFrameMaker.getPercent(1,1));
	}// end makeGif method
}