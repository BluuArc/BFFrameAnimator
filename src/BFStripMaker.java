import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
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
import java.io.File;

public class BFStripMaker {
	public static String className = "BFStripMaker";
	
	public static void main(String[] args) {
		System.out.println("Begin Program Execution of " + className + "\n");
		
		//agenda
		//TODO: fix parsing CGS
		
		
		//set variables
		boolean useArgs = false;			//options for use of command line arguments
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
			//get unitIDs
			unitID = SimpleInput.getString("Enter the unit IDs separated by spaces.");
			unitIDs = BFFrameMaker.parseString(unitID);
		} else if (args.length == 1) {
			// parse parameters
			/*
			 * sample input java BFStripMaker [list.txt] 
			 * [list.txt] = file of unit IDs, one ID per line 
			 */

			// set parameters
			listFile = args[0];

			// parse file into unitIDs array
			unitIDs = BFFrameMaker.parseList(listFile);
		} else if (args.length == 2) {
			// make 1 ID
			/*
			 * sample input java BFStripMaker [ID] [list.txt] 
			 * [ID] = unit ID to make 
			 * [list.txt] = file of unit IDs, one ID per line
			 */
			// set parameters
			unitIDs[0] = args[0];
			listFile = args[1];
		}
		
		//exit
		if(unitIDs[0].equals("-1")){
			System.out.println("Exiting application.");
			return;
		}
		
		System.out.println("Preparing to make " + unitIDs.length + " animation strips...");
		
		// set directories
		if (!useArgs) {
			//manually set directory
			System.out.println("Choose directory that contains the units sorted by IDs. Strip will be saved here.");
			dir = FileChooser.pickAFile();
			dir = BFFrameMaker.getDirectory(dir);

			// set GIF directory
			System.out.println("Choosing target directory for GIFs...");
			System.out.println("Temporary frames will be saved within this directory.");
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
			
			//set variables
			unitID = unitIDs[u];
			String workingDir = dir + "\\" + unitID;
			String workingFile = "";
			
			//debug("workingDir is [" + workingDir + "]");
			
			System.out.println("\n[Preparing to make strips for " + unitID + "]");
			
			//get files
			System.out.println("Getting CSV files and sprite sheet");
			String[] cgsNames = BFFrameMaker.getFiles(workingDir, "cgs", ".csv");					
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
			System.out.println("Parsing CGG File");				 					//CGG file contains position data for all animations 
			int[][] CGGFrames = new int[BFFrameMaker.getNumFrames(workingFile)][];	//reference: frames[frame number][parameters for frame]
			BFFrameMaker.parseCSV(workingFile, CGGFrames);
			
			//for each CGS file, create strip
			for(int c = 0; c < cgsNames.length; ++c){
				//parse CGS file
				System.out.println("Parsing CGS File");
				workingFile = cgsNames[c];
				int [][] CGSFrames = new int[BFFrameMaker.getNumFrames(workingFile)][]; //CGS files contain frame order and delay for animation
				BFFrameMaker.parseCSV(workingFile, CGSFrames);
				
				Picture2[] GifFrames = new Picture2[1]; 	//array for current working set of frames
															//its length is the number of frames for that animation
				
				String type = BFStripAnimator.getType(workingFile, false);	//can be 1idle, 2move, 3atk, or original type like limit
				
				//make animation
				if(CGSFrames.length != 0){
					System.out.println("\n[Making [" + type + "] strip for " + unitID + "]");
					GifFrames = new Picture2[CGSFrames.length];	//resize array to correct length of animation
					
					//make frames from sprite 
					for(int i = 0; i < CGSFrames.length; ++i){
						GifFrames[i] = makeFrame(unitID, sSheet, CGGFrames, CGSFrames, i); 
					}
					
					//crop and save frames
					makeNewFrame(GifFrames, unitID, CGSFrames, type);
					
					//make strip from frames
					makeStrip(dirGif, dir, unitID, type, CGSFrames);
				}else{
					System.out.println("ERROR: File error with [" + workingFile + "]");
				}
				System.out.println("\n");
			}//end for each CGS file
			System.out.println("[Finished making strips for " + unitID + "]");
		} // end for

		// reset directory
		BFFrameMaker.setDirectory(dir);
		
		System.out.println("\nEnd Program Execution of " + className + "\n");
		if(useArgs) //return to animation menu
			return;
		else
			System.exit(0);
	}//end main
	
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
		part.setAllPixelsToAnAlpha(0);

		BFFrameMaker.printProgress("Copying frames from spritesheet. Status: ", BFFrameMaker.getPercent(counter, csvFile.length));


		//make frame part by part
		for (int f = 0; f < numParts; f++) {
			part = copyPart(sSheet, part, frame, (numParts - 1 - f));
		}

		BFFrameMaker.printProgress("Copying frames from spritesheet. Status: ", BFFrameMaker.getPercent(counter + 1, csvFile.length));

		return part;
	}// end makeFrame method
	
	public static Picture2 rotateImage(int angle, Picture2 input, int centerX, int centerY){
		//based off of http://stackoverflow.com/questions/8639567/java-rotating-images
		//temporary copy
		Picture2 temp = new Picture2(input.getWidth(), input.getHeight());
		
		//rotate/transform
		double rotationRequired = Math.toRadians (angle);
		double locationX = centerX;
		double locationY = centerY;
		AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);

		// Drawing the rotated image at the required drawing locations
		temp.getGraphics().drawImage(op.filter((BufferedImage) input.getImage(), null), 0, 0, null);
		return temp;
		
	}//end rotateImage
	
	// method to copy a current part from a spritesheet to a picture2 object
	public static Picture2 copyPart(Picture2 sSheet, Picture2 part, int[] frame, int currentPart) {
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
		
		//offset pixels are positioned relative to center of frame; these are coords of top left of part
		int offsetX = (part.getWidth() / 2) + frameX;
		int offsetY = (part.getHeight() / 2) + frameY;

		int x = 0;
		int y = 0;
		for (x = 0; x < w; x++) {
			for (y = 0; y < h; y++) {
				// get pixels at current position
				int sourceX = spriteX + x;
				int sourceY = spriteY + y;
				if (sourceX >= sSheet.getWidth())
					sourceX = sSheet.getWidth() - 1;
				if (sourceY >= sSheet.getHeight())
					sourceY = sSheet.getHeight() - 1;
				sourcePix = sSheet.getPixel(sourceX, sourceY);
				
				//set target pixels
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
					targetPix.setColor(new Color(r, g, b));
					if(targetPix.getAlpha() == 0)
						targetPix.setAlpha((int)(pixval * opacity));
				}else if(opacity > 0 && a > 0){
					targetPix.setColor(new Color(r, g, b));
					if(targetPix.getAlpha() == 0 || a > targetPix.getAlpha())
						targetPix.setAlpha((int)(a * opacity));
				}//end else if
			} // end y
		} // end x
		
		//flip image
		//0 is no flip, 3 is flip horizontally and vertically (taken care of after this block)
		if(flip != 0 || flip != 3){
			//temporary copy
			Picture2 temp = new Picture2(tempPart.getWidth(), tempPart.getHeight());
			
			for (x = 0; x < w; x++) {
				for (y = 0; y < h; y++) {
					//get source pixel at current position
					int sourceX = offsetX + x;
					int sourceY = offsetY + y;
					sourcePix = tempPart.getPixel(sourceX, sourceY);
					
					//get target pixel position
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
		
		//flip vertical and horizontal
		if(flip == 3)
			tempPart = rotateImage(180, tempPart, offsetX + (w/2), offsetY + (h/2));


		if(rotate != 0){
			tempPart = rotateImage(rotate, tempPart, offsetX + (w/2), offsetY + (h/2));
		}//end rotate
		
		//necessary to keep proper alpha values
		part.getGraphics().drawImage(tempPart.getImage(), 0, 0, null);

		return part;
	}// end copyPart method
	
	// method to resize frames in array from given bounds
	public static void makeNewFrame(Picture2[] frame, String unitID, int[][] csvFile, String type) {
		int[] bounds = getBounds(frame);
		int w = bounds[3] - bounds[2];
		int h = bounds[1] - bounds[0];

		for (int i = 0; i < frame.length; i++) {
			BFFrameMaker.printProgress("Cropping and saving frames. Status: ", BFFrameMaker.getPercent(i, frame.length));

			//copy old part to new resized part
			Picture2 part = new Picture2(w, h);
			BFFrameMaker.copyPicture(frame[i], bounds[2], bounds[0], part, 0, 0, w, h);
			
			// save frame as ./unit_<unitID>_<type>-F<frameNumber>.png
			String fName = FileChooser.getMediaDirectory() + "\\unit_" + unitID + "_" + type + "-F" + i	+ ".png";
			part.write(fName);
			BFFrameMaker.printProgress("Cropping and saving frames. Status: ", BFFrameMaker.getPercent(i + 1, frame.length));
		}//end for each frame
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
					// save y coord if it's lower than the previously saved coord
					// and if there's something there (alpha != 0)
					if ((y < upperCoord)&& (p.getAlpha()!= 0))															// 0))
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

					// save y coord if it's greater than the previously saved coord
					// and if there's something there (alpha != 0)
					if ((y > lowerCoord)&& (p.getAlpha()!= 0))															// 0))
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

					// save x coord if it's lower than the previously saved coord
					// and if there's something there (alpha != 0)
					if ((x < upperCoord)&& (p.getAlpha()!= 0))															// 0))
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

					// save x coord if it's greater than the previously saved coord
					// and if there's something there (alpha != 0)
					if ((x > lowerCoord) && (p.getAlpha()!= 0))
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

	// method to get all bounds
	public static int[] getBounds(Picture2[] part) {
		int[] bounds = new int[4];

		//TODO: improve this so that it's more accurate (i.e. not 25% increments)
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
	public static void makeStrip(String dirGif, String dirStrip, String unitID, String type, int[][] csvFile){
		String fName = dirStrip + "\\" + unitID + "\\unit_" + type + "_" + unitID;
		fName = fName + ".png";
		System.out.println("Save Path: " + fName);
		//save strip as unit_<type>_<unitID>.png
	
		//make blank strip
		Picture2 currentFrame = new Picture2(FileChooser.getMediaDirectory() + "\\unit_" + unitID+ "_" + type + "-F0.png");
		Picture2 strip = new Picture2(currentFrame.getWidth() * csvFile.length, currentFrame.getHeight());
		
		//copy frames onto strip
		for (int i = 0; i < csvFile.length; i++) { // for each line
			BFFrameMaker.printProgress("Creating " + BFStripAnimator.getFilename(fName) + ". Status: ",
					BFFrameMaker.getPercent(i, csvFile.length));
			
			currentFrame = new Picture2(FileChooser.getMediaDirectory() + "\\unit_" + unitID+ "_" + type + "-F" + i + ".png");
			BFFrameMaker.copyPicture(currentFrame, 0, 0, strip, currentFrame.getWidth() * i, 0, currentFrame.getWidth() * (i + 1), currentFrame.getHeight());
			
			BFFrameMaker.printProgress("Creating " + BFStripAnimator.getFilename(fName) + ". Status: ",
					BFFrameMaker.getPercent(i + 1, csvFile.length));
		}
		
		strip.write(fName);
		
		//delete used frames
		for(int i = 0; i < csvFile.length; ++i){
			BFFrameMaker.printProgress("Deleting old frames. Status: ", BFFrameMaker.getPercent(i, csvFile.length));
			File currFrame = new File(FileChooser.getMediaDirectory() + "\\unit_" + unitID+ "_" + type + "-F" + i + ".png");
			if(!currFrame.delete()){
				System.out.println("ERROR: Failed to delete [" + currFrame.toString() + "]");
			}
			BFFrameMaker.printProgress("Deleting old frames. Status: ", BFFrameMaker.getPercent(i+1, csvFile.length));
		}
	}// end makeGif method
}