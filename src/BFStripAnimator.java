
/*
 * Name: Joshua Castor
 * Started: January 29, 2016
 *   Note: the wiki version was merged with this on Fev 27, 2016
 * Description: Using a CSV file, create frames from parameters on each line
 * 
 * important parts:  1 = part_count, 2,3 = x_pos,y_pos (relative to center of frame),
 *                   8,9 = img_x, img_y (top left corner of selection on spritesheet),
 * 
 */

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class BFStripAnimator {
	public static void main(String[] args) throws Exception {
		System.out.println("Begin Program Execution\n");

		// set variables
		boolean wiki = false;
		boolean useArgs = false;
		int wikiOption;
		String unitID;
		String[] unitIDs = new String[1];
		String mainDir, dirFrame, dirGif;
		String listFile = "";

		if (args.length != 0)
			useArgs = true;

		if (!useArgs) {
			wikiOption = SimpleInput
					.getIntNumber("What are you making? (0 for all, 1 for wiki, anything else to exit)");
			wiki = BFFrameMaker.setOpacity(wikiOption);

			if (wiki) {
				System.out.println("Making idle GIFs for the wiki");
			} else {
				System.out.println("Making all animations");
			}

			// get unit ids
			unitID = SimpleInput.getString("Enter the unit ID. The wiki option is " + wiki);
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

		// set directory
		System.out.println("Preparing to make " + unitIDs.length + " set(s) of GIFs...");

		if (!useArgs) {
			// set image directory
			System.out.println("Choose directory of unit images that contains the unit IDs");
			mainDir = FileChooser.pickAFile();
			BFFrameMaker.setDirectory(mainDir);
			mainDir = BFFrameMaker.getDirectory(mainDir);

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
			mainDir = BFFrameMaker.getDirectory(listFile) + "\\Units"; // units
																		// directory
			dirGif = BFFrameMaker.getDirectory(listFile) + "\\GIFs\\output";
		}
		dirFrame = dirGif + "\\frames";
		BFFrameMaker.setDirectory(dirFrame + "\\");
		System.out.println("Frames will be saved to " + dirFrame);

		// generate GIFs for each ID
		for (int u = 0; u < unitIDs.length; u++) {
			unitID = unitIDs[u];

			String[] fNames = setup(mainDir, unitID);

			// create arrays
			int[] numFrames = new int[3]; // idle, move, atk
			for (int i = 0; i < 3; i++)
				numFrames[i] = BFFrameMaker.getNumFrames(fNames[i]);

			System.out.println("\n[Parsing CSV Files for " + unitID + "]");
			System.out.println("Parsing idle CSV File");
			int[][] idle = new int[numFrames[0]][];
			BFFrameMaker.parseCSV(fNames[0], idle);

			Picture2[] frame = new Picture2[1];
			if (!wiki) {// all three animations with properly cropped borders
				System.out.println("Parsing move CSV File");
				int[][] move = new int[numFrames[1]][];
				BFFrameMaker.parseCSV(fNames[1], move);

				System.out.println("Parsing attack CSV File");
				int[][] atk = new int[numFrames[2]][];
				BFFrameMaker.parseCSV(fNames[2], atk);

				if (idle.length != 0) {
					System.out.println("\n[Making idle GIF for " + unitID + "]");
					String type = getType(fNames[0]);
					frame = extractFrames(fNames[3], unitID, type, numFrames[0], wiki);
					BFFrameMaker.makeNewFrame(frame, unitID, idle, type);
					// System.out.println("Making idle GIF for " + unitID + "
					// ...");
					makeGif(dirGif, unitID, type, idle, wiki);
				} else {
					System.out.println("No idle CSV file found for " + unitID + ".");
				}
				System.out.println("\n");
				if (move.length != 0) {
					System.out.println("[Making movement GIF for " + unitID + "]");
					String type = getType(fNames[1]);
					frame = extractFrames(fNames[4], unitID, type, numFrames[1], wiki);
					BFFrameMaker.makeNewFrame(frame, unitID, move, type);
					// System.out.println("Making move GIF for " + unitID + "
					// ...");
					makeGif(dirGif, unitID, type, move, wiki);
				} else {
					System.out.println("No move CSV file found for " + unitID + ".");
				}
				System.out.println("\n");
				if (atk.length != 0) {
					System.out.println("[Making attack GIF for " + unitID + "]");
					String type = getType(fNames[2]);
					frame = extractFrames(fNames[5], unitID, type, numFrames[2], wiki);
					BFFrameMaker.makeNewFrame(frame, unitID, atk, type);
					// System.out.println("Making atk GIF for " + unitID + "
					// ...");
					makeGif(dirGif, unitID, type, atk, wiki);
				} else {
					System.out.println("No atk CSV file found for " + unitID + ".");
				}
				System.out.println("\n");
			} else { // wiki idle GIFs
				if (idle.length != 0) {
					System.out.println("\n[Making idle GIF for " + unitID + "]");
					String type = getType(fNames[0]);
					frame = extractFrames(fNames[3], unitID, type, numFrames[0], wiki);
					makeNewFrameWiki(frame, unitID, type, idle);
					// System.out.println("Making idle GIF for " + unitID + "
					// ...");
					makeGif(dirGif, unitID, type, idle, wiki);
				} else {
					System.out.println("No idle CSV file found for " + unitID + ".");
				}
			}
		} // end for

		// reset directory
		BFFrameMaker.setDirectory(mainDir);

		System.out.println("Don't forget to clear out the frames folder.");

		System.out.println("\nEnd Program Execution");
	} // end main method

	////////////////////////////// methods\\\\\\\\\\\\\\\\\\\\\\\\\\

	// method to initialize required pictures and files
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

	public static String getFilename(String fName) {
		int pos = fName.lastIndexOf(File.separatorChar);
		fName = fName.substring(pos + 1, fName.length());// becomes
															// unit_cgg_10011.csv
		return fName;
	}

	// method to get corresponding CSV file of sprite sheet
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
	public static String getType(String fName) { // ex input is
													// /some/dir/unit_atk_cgs_10273.csv
		int pos = fName.lastIndexOf(File.separatorChar);
		fName = fName.substring(pos + 1, fName.length()); // becomes
															// unit_atk_cgs_10273.csv
		pos = fName.indexOf("_", 0); // get location of first underscore from
										// index 0
		String type = fName.substring(pos + 1, fName.indexOf("cgs", pos + 1) - 1); // becomes
																					// atk
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
		return type;
	}// end getUnitID method

	// method to resize frames in array from given bounds
	public static void makeNewFrame(Picture2[] frame, String unitID, String type, int[][] csvFile) {
		int[] bounds = BFFrameMaker.getBounds(frame);
		int w = bounds[3] - bounds[2];
		int h = bounds[1] - bounds[0];

		for (int i = 0; i < frame.length; i++) {
			// int currentFrame = csvFile[i][0];
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
		/*
		 * int[] bounds = getBounds(frame); int w = bounds[3] - bounds[2]; int h
		 * = bounds[1] - bounds[0];
		 */

		for (int i = 0; i < frame.length; i++) {
			// int currentFrame = csvFile[i][0];
			int delay = BFFrameMaker.FramesToMilliseconds(csvFile[i][3]);

			BFFrameMaker.printProgress("Cropping and saving frames. Status: ",
					BFFrameMaker.getPercent(i, frame.length));
			// System.out.println("Cropping and saving frame number " +
			// (currentFrame + 1) + "...");
			// Picture2 part = new Picture2(w,h);
			// copyPicture(frame[i], bounds[2], bounds[0], part, 0, 0, w, h);
			// save frane as ./frame_<currentFrame>/part_<currentPart>.jpg
			// System.out.println("Saving frame number " + currentFrame +
			// "...");
			String fName = FileChooser.getMediaDirectory() + "\\unit_" + unitID + "_" + type + "-F" + i + "_" + delay
					+ ".png";
			frame[i].write(fName);
			BFFrameMaker.printProgress("Cropping and saving frames. Status: ",
					BFFrameMaker.getPercent(i, frame.length));
		}

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
		g.setTransparent(new Color(253, 237, 43));
		g.setRepeat(0);
		g.start(fName);
		for (int i = 0; i < csvFile.length; i++) { // for each line
			// System.out.println("Adding frame " + (i + 1) + " of " +
			// csvFile.length + " to GIF of " + unitID);
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
	}// end makeGif method

	public static Picture2[] extractFrames(String sSheetName, String unitID, String type, int numFrames, boolean wiki) {
		Picture2 sSheet = new Picture2(sSheetName);
		int width = sSheet.getWidth() / numFrames;
		int height = sSheet.getHeight();
		int diff = 0;
		if ((height <= 140) && wiki) {
			diff = 140 - height;
		}
		Picture2 newFrame[] = new Picture2[numFrames];
		for (int i = 0; i < numFrames; i++) {
			BFFrameMaker.printProgress("Extracting frames. Status: ", BFFrameMaker.getPercent(i, numFrames));
			// prepare template
			newFrame[i] = new Picture2(width, height + diff);
			newFrame[i].setAllPixelsToAColor(new Color(253, 237, 43));
			newFrame[i].setAllPixelsToAnAlpha(255);

			// copy frame
			Pixel sourcePix;
			Pixel targetPix;
			int y = 0;

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

					if (sourcePix.getAlpha() > 100) { // copy pixel if there's
														// something in the
														// source pixel
						targetPix.setColor(new Color(r, g, b));
						// if(sourcePix.getAlpha() > 100) //copy transparency if
						// there's a reasonable amount in the source
						targetPix.setAlpha(sourcePix.getAlpha());
					} // end if
				} // end for y
			} // end for x
			BFFrameMaker.printProgress("Extracting frames. Status: ", BFFrameMaker.getPercent(i + 1, numFrames));
		} // end for

		return newFrame;
	}// end extractFrame method

} // end of class