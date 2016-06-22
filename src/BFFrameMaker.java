
/*
 * Created by: Joshua Castor
 * Started: December 21, 2015 
 * Description: Using a CSV file, create frames from parameters on each line
 * 
 * important parts:  1 = part_count, 2,3 = x_pos,y_pos (relative to center of frame),
 *                   8,9 = img_x, img_y (top left corner of selection on spritesheet),
 * 
 * This program is licensed under the Creative Commons Attribution 3.0 United States License.
 * Visit https://github.com/BluuArc/BFFrameAnimator for updates.
 */

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;

public class BFFrameMaker {
	public static Color transparentColor = new Color(253, 237, 43);
	
	public static void main(String[] args) throws Exception {
		System.out.println("Begin Program Execution\n");

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

		// if no commandline parameters are given
		if (!useArgs) {
			
			//set opacity option
			opacOption = SimpleInput
					.getIntNumber("Would you like to use opacity? (0 for no, 1 for yes, anything else to exit)");
			if(opacOption != 0 && opacOption != 1){
				System.out.println("Exiting application.");
				return;
			}
			useOpacity = setOpacity(opacOption);

			//output result (mostly for debugging  purposes)
			if (useOpacity)
				System.out.println("Opacity option is on.\n");
			else
				System.out.println("Opacity option is off.\n");

			//get unitIDs
			unitID = SimpleInput.getString("Enter the unit IDs separated by spaces. The opacity option is " + useOpacity);
			unitIDs = parseString(unitID);

		} else if (args.length == 2) {
			// parse parameters
			/*
			 * sample input java BFFrameMaker [list.txt] [opacity] 
			 * [list.txt] = file of unit IDs, one ID per line [opacity] = option for opacity;
			 * 1 for true, 0 for false
			 */

			// set parameters
			listFile = args[0];
			opacOption = Integer.parseInt(args[1]);
			useOpacity = setOpacity(opacOption);

			// parse file into unitIDs array
			unitIDs = parseList(listFile);
		} else if (args.length == 3) {
			// make 1 ID
			/*
			 * sample input java BFFrameMaker [ID] [list.txt] [opacity] 
			 * [ID] = unit ID to make 
			 * [list.txt] = file of unit IDs, one ID per line
			 * [opacity] = option for opacity; 1 for true, 0 for false
			 */
			
			// set parameters
			unitIDs[0] = args[0];
			listFile = args[1];
			opacOption = Integer.parseInt(args[2]);
			useOpacity = setOpacity(opacOption);
		}
		
		//exit
		if(unitIDs[0].equals("-1")){
			System.out.println("Exiting application.");
			return;
		}

		System.out.println("Preparing to make " + unitIDs.length + " set(s) of GIFs...");

		// set directories
		if (!useArgs) {
			//manually set directory
			System.out.println("Choose directory that contains the units sorted by IDs.");
			dir = FileChooser.pickAFile();
			dir = getDirectory(dir);

			// set GIF directory
			System.out.println("Choosing target directory for GIFs...");
			dirGif = FileChooser.pickAFile();
			dirGif = getDirectory(dirGif);
		} else {
			// use list file to set directories
			dir = getDirectory(listFile) + "\\Units"; // units directory
			dirGif = getDirectory(listFile) + "\\GIFs\\output";
		}
		dirFrame = dirGif + "\\frames";
		setDirectory(dirFrame + "\\");
		System.out.println("Frames will be saved to " + dirFrame);

		// begin creating sets of animations one by one
		for (int u = 0; u < unitIDs.length; u++) {

			//set variables
			unitID = unitIDs[u];
			String[] fNames = setup(dir, unitID);
			Picture2 sSheet = new Picture2(fNames[4]);

			// create arrays
			int[] numFrames = new int[4]; // integer array to get number of
											// frames/lines of each CSV file
			for (int i = 0; i < numFrames.length; i++)
				numFrames[i] = getNumFrames(fNames[i]);

			System.out.println("\n[Parsing CSV Files for " + unitID + "]");
			System.out.println("Parsing CGG File"); 	//CGG file contains position data for all animations 
			int[][] frames = new int[numFrames[0]][]; // reference: frames[frame number][parameters for frame]
			parseCSV(fNames[0], frames);

			//CGS files contain frame order and delay for animation
			//TODO: don't hardcode names, but instead look for CGS files and parse them
			System.out.println("Parsing idle CGS File");
			int[][] idle = new int[numFrames[1]][];
			parseCSV(fNames[1], idle);

			System.out.println("Parsing move CGS File");
			int[][] move = new int[numFrames[2]][];
			parseCSV(fNames[2], move);

			System.out.println("Parsing attack CSV File");
			int[][] atk = new int[numFrames[3]][];
			parseCSV(fNames[3], atk);

			Picture2[] frame = new Picture2[1]; //array for current working set of frames
												//its length is the number of frames for that animation

			//make animations
			if (idle.length != 0) {
				System.out.println("\n[Making idle GIF for " + unitID + "]");
				frame = new Picture2[idle.length];
				for (int i = 0; i < idle.length; i++) {
					//make frames from spritesheet
					frame[i] = makeFrame(unitID, sSheet, frames, idle, i, useOpacity);
				}
				//crop and save frames
				makeNewFrame(frame, unitID, idle, "1idle");
				
				//make GIF from frames
				makeGif(dirGif, unitID, "1idle", idle, useOpacity);
			} else {
				System.out.println("No idle CSV file found for " + unitID + ".");
			}
			System.out.println("\n");
			if (move.length != 0) {
				System.out.println("[Making movement GIF for " + unitID + "]");
				frame = new Picture2[move.length];
				for (int i = 0; i < move.length; i++) {
					//make frames from spritesheet
					frame[i] = makeFrame(unitID, sSheet, frames, move, i, useOpacity);
				}
				//crop and save frames
				makeNewFrame(frame, unitID, move, "2move");
				
				//make GIF from frames
				makeGif(dirGif, unitID, "2move", move, useOpacity);
			} else {
				System.out.println("No movement CSV file found for " + unitID + ".");
			}
			System.out.println("\n");
			if (atk.length != 0) {
				System.out.println("[Making attack GIF for " + unitID + "]");
				frame = new Picture2[atk.length];
				for (int i = 0; i < atk.length; i++) {
					//make frames from spritesheet
					frame[i] = makeFrame(unitID, sSheet, frames, atk, i, useOpacity);
				}
				//crop and save frames
				makeNewFrame(frame, unitID, atk, "3atk");
				
				//make GIF from frames
				makeGif(dirGif, unitID, "3atk", atk, useOpacity);
			} else {
				System.out.println("No attack CSV file found for " + unitID + ".");
			}
			System.out.println("\n");
		} // end for each unit ID

		// reset directory
		setDirectory(dir);

		//TODO: automatically delete frames
		System.out.println("Don't forget to clear out the frames folder in " + dirFrame);

		System.out.println("\nEnd Program Execution");
	} // end main method

	////////////////////////////// methods\\\\\\\\\\\\\\\\\\\\\\\\\\

	// method to setMediaPath using a picture, assuming that the images are in
	// one directory
	public static void setDirectory(String fName) {
		int pos = fName.lastIndexOf(File.separatorChar);
		String dir = fName.substring(0, pos);
		FileChooser.setMediaPath(dir);
	}// end setDirectory method

	// method to get directory as a string
	public static String getDirectory(String fName) {
		int pos = fName.lastIndexOf(File.separatorChar);
		String dir = fName.substring(0, pos);
		return dir;
	}// end setDirectory method

	// method to initialize required pictures and files
	public static String[] setup(String dir, String unitID) {
		String[] fNames = new String[5];

		// declare files and pictures
		// TODO: change so that names aren't hardcoded
		fNames[0] = dir + "\\" + unitID + "\\unit_cgg_" + unitID + ".csv";
		fNames[1] = dir + "\\" + unitID + "\\unit_idle_cgs_" + unitID + ".csv";
		fNames[2] = dir + "\\" + unitID + "\\unit_move_cgs_" + unitID + ".csv";
		fNames[3] = dir + "\\" + unitID + "\\unit_atk_cgs_" + unitID + ".csv";
		fNames[4] = dir + "\\" + unitID + "\\unit_anime_" + unitID + ".png";

		return fNames;
	}// end setup method

	// method to set opacity option based on input
	public static boolean setOpacity(int opacOption) {
		// set opacity
		if (opacOption == 1)
			return true;
		else
			return false;
	}// end setOpacity

	// method to print progress; assumes that you are on the line to edit
	public static void printProgress(String preText, int currProgress) {
		// Sample input: "Status: "[bar goes here]
		if (currProgress <= 100)
			System.out.printf("\r%s%03d%c", preText, currProgress, '%');
		if (currProgress >= 100)
			System.out.printf(" - Done\n");
	}// end printProgress

	//method to get percentage 
	public static int getPercent(int curr, int total) {
		return (int) (((double) (curr) / (double) total) * 100);
	}

	// method to get unit ID to call other files
	public static String getUnitID(String fName) { // ex input is
													// /some/dir/unit_cgg_10011.csv
		
		/*
		int pos = fName.lastIndexOf(File.separatorChar);
		fName = fName.substring(pos + 1, fName.length());// becomes
															// unit_cgg_10011.csv
		*/
		fName = BFStripAnimator.getFilename(fName);
		String unitID = fName.substring(fName.lastIndexOf("_") + 1, fName.lastIndexOf(".")); // becomes
																								// 10011
		return unitID;
	}// end getUnitID method

	// method to get number of frames (number of lines in CSV file)
	public static int getNumFrames(String csvFile) {
		BufferedReader br = null;
		@SuppressWarnings("unused")
		String line = " ";
		int numFrames = 0;

		try {
			//open file
			br = new BufferedReader(new FileReader(csvFile));
			
			//while not at EOF, increment line counter
			while ((line = br.readLine()) != null) {
				numFrames++;
			} // end while
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: File [" + csvFile + "] not found");
		} catch (IOException e) {
			System.out.println("ERROR: IO Exception when opening/reading file [" + csvFile + "]");
		} finally {
			//close file
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					System.out.println("ERROR: IO Exception when closing file [" + csvFile + "]");
				} // end catch
			} // end try
		} // end finally

		return numFrames;
	}// end getNumFrames method

	// method to parse a String input separated by spaces into a String array
	public static String[] parseString(String input) {
		String[] output = new String[1];
		if (input.length() > 7) //a regular unit ID is 5-6 characters long and a space is 1 character
			output = input.split(" ");
		else
			output[0] = input;

		return output;
	}// end parseString method

	// method to parse a file with a list of unit IDs into a string array
	public static String[] parseList(String fName) {
		//each index represents the line number in the file minus one
		//e.g. line 1 has index 0, line 2 has index 1, etc.
		String[] output = new String[getNumFrames(fName)];
		BufferedReader br = null;
		String line = " ";
		int i = 0;

		try {
			//open file
			br = new BufferedReader(new FileReader(fName));
			//while not at EOF
			while ((line = br.readLine()) != null) {
				// put each line into output array
				output[i] = line;
				i++;
			} // end while
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: File [" + fName + "] not found");
		} catch (IOException e) {
			System.out.println("ERROR: IO Exception when opening/reading file [" + fName + "]");
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					System.out.println("ERROR: IO Exception when closing file [" + fName + "]");
				} // end catch
			} // end try
		} // end finally

		return output;
	}// end parseString method

	// method to parse CSV into a String array where each line
	public static void parseCSV(String csvFile, int[][] frames) {
		//output to frames 2D array
		BufferedReader br = null;
		String line = " ";
		String separator = ",";
		int i = 0;

		try {
			//open file
			br = new BufferedReader(new FileReader(csvFile));
			//while not at EOF
			while ((line = br.readLine()) != null) {// for each line that's not
													// blank
				// convert line from string array to int array
				String[] frame = line.split(separator);
				int[] parsedFrame = convertToInt(frame);

				// put each line into main array of frames
				frames[i] = new int[parsedFrame.length];
				frames[i] = parsedFrame;

				i++;
			} // end while
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: File [" + csvFile + "] not found");
		} catch (IOException e) {
			System.out.println("ERROR: IO Exception when opening/reading file [" + csvFile + "]");
		} finally {
			//close file
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// e.printStackTrace();
					System.out.println("ERROR: IO Exception when closing file [" + csvFile + "]");
				} // end catch
			} // end try
		} // end finally
	}// end parseCSV method

	// method to convert string array to integer array
	public static int[] convertToInt(String[] input) {
		int[] frame = new int[input.length];
		for (int i = 0; i < input.length; i++)
			frame[i] = Integer.parseInt(input[i]);
		return frame;
	}// end convertToInt method

	// method to save copy a current part from a spritesheet to a picture2 object
	public static Picture2 copyPart(Picture2 sSheet, Picture2 part, int[] frame, int currentPart, boolean useOpacity) {
		//get data for current frame
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
		
		//opacity ranges from 0.0 to 1.0 since it's a multiplier
		double opacity = opac / 100.0;

		//tempPart is necessary to keep proper transparency values
		Picture2 tempPart = new Picture2(part.getWidth(),part.getHeight());
		Pixel sourcePix;
		Pixel targetPix;

		int offsetX = (part.getWidth() / 2) + frameX;
		int offsetY = (part.getHeight() / 2) + frameY;

		int x = 0;
		int y = 0;
		for (x = 0; x < w; x++) {
			for (y = 0; y < h; y++) {
				// get current pixels
				int sourceX = spriteX + x;
				int sourceY = spriteY + y;
				if (sourceX >= sSheet.getWidth())
					sourceX = sSheet.getWidth() - 1;
				if (sourceY >= sSheet.getHeight())
					sourceY = sSheet.getHeight() - 1;
				sourcePix = sSheet.getPixel(sourceX, sourceY);

				//get target pixels
				int targetX = offsetX;
				int targetY = offsetY;
				targetX += x;
				targetY += y;
				targetPix = tempPart.getPixel(targetX, targetY);
				
				//get source color values
				int r, g, b, a;
				r = sourcePix.getRed();
				g = sourcePix.getGreen();
				b = sourcePix.getBlue();
				a = sourcePix.getAlpha();
				
				//set alpha/opacity
				int targetAlpha;
				if(useOpacity)
					targetAlpha = (int)(a * opacity);
				else
					targetAlpha = a;
				
				//set colors and opacity according to blend mode
				if((blendMode == 1) && opacity > 0 && a > 0){
					//blend code based off of this: http://pastebin.com/vXc0yNRh
					int pixval = (r + g + b) / 3;
					r += pixval;
					g += pixval;
					b += pixval;
					
					if(r > 255)
						r = 255;
					if(g > 255)
						g = 255;
					if(b > 255)
						b = 255;
					
					if(useOpacity)
						targetAlpha = (int)(pixval * opacity);
				}
				if (targetAlpha > 150 && opacity > 0) { 
					// copy pixel to target if there's something in the source pixel
					targetPix.setColor(new Color(r, g, b));
					if (a > targetPix.getAlpha())
						//condition to keep proper transparency values
						targetPix.setAlpha(targetAlpha);
				} // end if
			} // end y
		} // end x
		

		//flip image
		//0 is no flip, 3 is flip horizontally and vertically (taken care of after this block)
		if(flip != 0 || flip != 3){
			//temporary copy
			Picture2 temp = new Picture2(tempPart.getWidth(), tempPart.getHeight());
			
			for (x = 0; x < w; x++) {
				for (y = 0; y < h; y++) {
					//get source pixels
					int sourceX = offsetX + x;
					int sourceY = offsetY + y;
					sourcePix = tempPart.getPixel(sourceX, sourceY);
					
					//set target pixels
					int targetX = offsetX;
					int targetY = offsetY;
					
					//horizontal flip
					if (flip == 1)
						targetX += (w - 1 - x);
					else
						targetX += x;

					//vertical flip
					if (flip == 2)
						targetY += (h - 1 - y);
					else
						targetY += y;
					
					//copy pixels at flipped coordinates
					targetPix = temp.getPixel(targetX, targetY);
					targetPix.setColor(sourcePix.getColor());
					targetPix.setAlpha(sourcePix.getAlpha());
				}//end y
			}//end x

			//set tempPart as new copy
			tempPart = temp;
		}//end flip
		
		
		//flip vertically and horizontally
		if(flip == 3)
			tempPart = BFStripMaker.rotateImage(180, tempPart, offsetX + (w/2), offsetY + (h/2));

		//rotate image
		if(rotate != 0){
			tempPart = BFStripMaker.rotateImage(rotate, tempPart, offsetX + (w/2), offsetY + (h/2));
		}//end rotate
		
		//necessary command to keep proper alpha values
		part.getGraphics().drawImage(tempPart.getImage(), 0, 0, null);

		return part;
	}// end copyPart method

	// method to get the largest height of all frames
	public static int getLargestHeight(int[][] frames, int[][] csvFile) {
		int h1 = 65; // default height is 140

		// for each frame
		for (int i = 0; i < csvFile.length; i++) {
			// for each part
			for (int j = 0; j < frames[csvFile[i][0]][1]; j++) {
				// get the height
				int testHeight = frames[csvFile[i][0]][3 + (j * 11)];

				/*
				 * //find farthest value of height if it's negative by changing
				 * the sign and adding the height of the part if(testHeight < 0)
				 * testHeight = (testHeight - frames[csvFile[i][0]][11 + (j *
				 * 11)]) * -1;
				 */

				if (testHeight > 0)
					testHeight = testHeight * -1;

				testHeight = (testHeight - frames[csvFile[i][0]][11 + (j * 11)]) * -1;

				// compare the height
				if (testHeight > h1) {
					h1 = testHeight;
				} // end if
			} // end for each part
		} // end for each frame
		if ((h1 % 10) != 0)
			h1 = ((h1 + 9) / 10) * 10; // round up to the nearest 5th
		else
			h1 = h1 + 10;
		return h1 * 2;
	}// end getLargestHeight method

	// method to get the largest width of all frames
	public static int getLargestWidth(int[][] frames, int[][] csvFile) {
		int w1 = 65; // default width is 140

		// for each frame
		for (int i = 0; i < csvFile.length; i++) {
			// for each part
			for (int j = 0; j < frames[csvFile[i][0]][1]; j++) {
				// get the width
				int testWidth = frames[csvFile[i][0]][2 + (j * 11)];
				/*
				 * //find farthest value of width if it's positive by adding the
				 * width of the part if(testWidth >= 0) testWidth = (testWidth +
				 * frames[csvFile[i][0]][10 + (j * 11)]); else testWidth =
				 * testWidth * -1;
				 */

				if (testWidth < 0)
					testWidth = testWidth * -1;

				testWidth = (testWidth + frames[csvFile[i][0]][10 + (j * 11)]);

				// compare the height
				if (testWidth > w1) {
					w1 = testWidth;
				} // end if
			} // end for each part
		} // end for each frame
		if ((w1 % 10) != 0)
			w1 = ((w1 + 9) / 10) * 10; // round up to the nearest 5th
		else
			w1 = w1 + 10;
		return w1 * 2;
	}// end getLargestWidthmethod

	// method to make a frame from a spritesheet and save it to the right
	// directory
	public static Picture2 makeFrame(String unitID, Picture2 sSheet, int[][] frames, int[][] csvFile, int counter,
			boolean useOpacity) {
		int currentFrame = csvFile[counter][0];
		int[] frame = frames[currentFrame];
		int numParts = frame[1];

		// create general template for all frames
		int width = getLargestWidth(frames, csvFile);
		int height = getLargestHeight(frames, csvFile);
		Picture2 part = new Picture2(width, height);	//resulting frame
		part.setAllPixelsToAColor(transparentColor);
		part.setAllPixelsToAnAlpha(255);

		printProgress("Copying frames from spritesheet. Status: ", getPercent(counter, csvFile.length));

		//make frame part by part
		for (int f = 0; f < numParts; f++) {
			part = copyPart(sSheet, part, frame, (numParts - 1 - f), useOpacity);
		}

		printProgress("Copying frames from spritesheet. Status: ", getPercent(counter + 1, csvFile.length));

		return part;
	}// end makeFrame method

	// method to convert frames to milliseconds
	public static int FramesToMilliseconds(int frames) {
		double fps = 60.0;
		int mSec = (int) ((frames / fps) * 1000);
		return mSec;
	}// end FramesToMilliseconds method

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
					// save y coord if it's lower than the previously saved coord
					// and if it's not the transparent color
					if ((y < upperCoord) && (Pixel.colorDistance(c, transparentColor) != 0))
						upperCoord = y;
				} // end y
			} // end x
		} // end i

		if ((upperCoord % 10) != 0)
			upperCoord = ((upperCoord - 9) / 10) * 10;
		else
			upperCoord = upperCoord - 10;

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
					// save y coord if it's lower than the previously saved coord
					// and if it's not the transparent color
					if ((y > lowerCoord) && (Pixel.colorDistance(c, transparentColor) != 0))
						lowerCoord = y;
				} // end y
			} // end x
		} // end i

		if ((lowerCoord % 10) != 0)
			lowerCoord = ((lowerCoord + 9) / 10) * 10;
		else
			lowerCoord = lowerCoord + 10;

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
					Color c = p.getColor();
					// save y coord if it's lower than the previously saved coord
					// and if it's not the transparent color
					if ((x < upperCoord) && (Pixel.colorDistance(c, transparentColor) != 0))
						upperCoord = x;
				} // end y
			} // end x
		} // end i

		if ((upperCoord % 10) != 0)
			upperCoord = ((upperCoord - 9) / 10) * 10;
		else
			upperCoord = upperCoord - 10;

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
					// save y coord if it's lower than the previously saved coord
					// and if it's not the transparent color
					if ((x > lowerCoord) && (Pixel.colorDistance(c, transparentColor) != 0))
						lowerCoord = x;
				} // end y
			} // end x
		} // end i

		if ((lowerCoord % 10) != 0)
			lowerCoord = ((lowerCoord + 9) / 10) * 10;
		else
			lowerCoord = lowerCoord + 10;

		if (lowerCoord > maxW)
			lowerCoord = maxW;

		return lowerCoord;
	}// end getLowerBound method

	// method to get all boundaries
	public static int[] getBounds(Picture2[] part) {
		int[] bounds = new int[4];

		printProgress("Getting bounds. Status: ", 0);
		bounds[0] = getUpperBound(part);

		printProgress("Getting bounds. Status: ", 25);
		bounds[1] = getLowerBound(part);

		printProgress("Getting bounds. Status: ", 50);
		bounds[2] = getLeftBound(part);

		printProgress("Getting bounds. Status: ", 75);
		bounds[3] = getRightBound(part);

		printProgress("Getting bounds. Status: ", 100);

		return bounds;
	}// end getBounds method

	// method to resize frames in array to new dimensions
	public static void makeNewFrame(Picture2[] frame, String unitID, int[][] csvFile, String type) {
		int[] bounds = getBounds(frame);
		int w = bounds[3] - bounds[2];
		int h = bounds[1] - bounds[0];

		for (int i = 0; i < frame.length; i++) {
			printProgress("Cropping and saving frames. Status: ", getPercent(i, frame.length));
			int delay = FramesToMilliseconds(csvFile[i][3]);
			
			//copy old part to new resized part
			Picture2 part = new Picture2(w, h);
			copyPicture(frame[i], bounds[2], bounds[0], part, 0, 0, w, h);
			
			// save frame as ./unit_<unitID>_<type>-F<frameNumber>_<delay>.png
			String fName = FileChooser.getMediaDirectory() + "\\unit_" + unitID + "_" + type + "-F" + i + "_" + delay
					+ ".png";
			part.write(fName);
			printProgress("Cropping and saving frames. Status: ", getPercent(i + 1, frame.length));
		}//end for each frame
	}// end makeNewFrame method

	// method to make an exact copy of an image onto another image using
	// coordinates, no color required
	public static void copyPicture(Picture2 p, int sX1, int sY1, Picture2 p2, int sX2, int sY2, int eX2, int eY2) {
		// declare variables
		Pixel pix;
		int x;
		int y;

		// for each row
		for (x = sX2; x < eX2; x++) {
			// for each column
			for (y = sY2; y < eY2; y++) {
				// store the color into a pixel in the result
				Pixel pix2;
				pix2 = p2.getPixel(x, y);
				// Color pix2Color = pix2.getColor();

				// access the pixel on the first image at a specified coordinate
				// location
				int pX = sX1 + (x - sX2);
				int pY = sY1 + (y - sY2);
				pix = p.getPixel(pX, pY);
				Color pixColor = pix.getColor();

				// set pixel color in the final image
				pix2.setColor(pixColor);
				pix2.setAlpha(pix.getAlpha());
			} // end for y
		} // end for x
	}// end copyPicture method

	// method to make a GIF
	public static void makeGif(String dirGif, String unitID, String type, int[][] csvFile, boolean useOpacity)
			throws Exception {
		String fName = dirGif + "\\unit_" + unitID + "_" + type;
		if (!useOpacity)
			fName = fName + "_nopac";
		else
			fName = fName + "_opac";
		fName = fName + ".gif";
		
		//save gif as  unit_<unitID>_<type>_<n/opac>.gif
		//TODO: delete frames as they are used

		AnimatedGifEncoder g = new AnimatedGifEncoder();
		g.setQuality(1);
		g.setDispose(2);
		g.setTransparent(transparentColor);
		g.setRepeat(0);
		g.start(fName);
		for (int i = 0; i < csvFile.length; i++) {
			printProgress("Creating " + BFStripAnimator.getFilename(fName) + ". Status: ",
					getPercent(i, csvFile.length));
			int delay = FramesToMilliseconds(csvFile[i][3]);
			BufferedImage currentFrame = ImageIO.read(new File(FileChooser.getMediaDirectory() + "\\unit_" + unitID
					+ "_" + type + "-F" + i + "_" + delay + ".png"));
			g.setDelay(delay);
			g.addFrame(currentFrame);
			printProgress("Creating " + BFStripAnimator.getFilename(fName) + ". Status: ",
					getPercent(i + 1, csvFile.length));
		}
		g.finish();
	}// end makeGif method

} // end of class