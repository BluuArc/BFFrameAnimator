/*
 *	Rewrite of BFFrameMaker project to better fit OOP principles.
 * 
 *	Class that contains all frame data
 * 	Along with methods needed to set those values.
 *
 *	Started 12/2/2016
 * 
 *	@author Joshua Castor
 */

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
//import java.io.IOException;
//import java.io.BufferedReader;
//import java.awt.Graphics2D;
//import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
//import javax.imageio.ImageIO;
import java.lang.ArrayIndexOutOfBoundsException;

public class Frame{
	private static int[] dimensions = {0,0};
	private static int lowestPoint = -1;

	private int frameNumber;
	private int delay;
	private Part[] parts;
	private Picture2 image;	//one Picture2 object for every frame
	private String fileName;

	private class Part{
		private int frameX;		//start x on frame
		private int frameY;		//start y on frame
		private int flip;		//flip option
		private int blendMode;	//blend mode option
		private double opacity;	//opacity of part
		private int rotate;		//rotation of part in degrees
		private int spriteX;	//start x on sprite sheet
		private int spriteY;	//start y on sprite sheet
		private int width;		//width of frame
		private int height;		//height of frame
		private int page_id;	//number of sprite sheet to use
		private String partImageName;	

		public Part(int[] csvLine, int currentPart, String preName){
			frameX = csvLine[2 + (currentPart * 11)];
			frameY = csvLine[3 + (currentPart * 11)];
			flip = csvLine[4 + (currentPart * 11)];
			blendMode = csvLine[5 + (currentPart * 11)];
			opacity = csvLine[6 + (currentPart * 11)] / 100.0;
			rotate = csvLine[7 + (currentPart * 11)];
			spriteX = csvLine[8 + (currentPart * 11)];
			spriteY = csvLine[9 + (currentPart * 11)];
			width = csvLine[10 + (currentPart * 11)];
			height = csvLine[11 + (currentPart * 11)];
			page_id = csvLine[12 + (currentPart * 11)];
			partImageName = preName + "_part-" + currentPart + ".png";
		}

		@SuppressWarnings("unused")
		public void createPartImage(Picture2 sSheets[], int finalFrameX, int finalFrameY, boolean useOpacity){
			Picture2 sSheet = sSheets[page_id];
			Picture2 tempImage = new Picture2(finalFrameX, finalFrameY);
			Picture2 partImage = new Picture2(finalFrameX, finalFrameY);
			tempImage.setAllPixelsToAnAlpha(0);
			partImage.setAllPixelsToAnAlpha(0);

			Pixel sourcePix,targetPix;
			int centerX = partImage.getWidth()/2;
			int centerY = partImage.getHeight()/2;
			int startX = centerX - width/2;
			int startY = centerY - height/2;
			int diff = finalFrameY - lowestPoint;
			int x,y;
			int newWidth = width;
			int newHeight = height;

			//copy part to center of image
			for(y = 0; y < height; ++y){
				for(x = 0; x < width; ++x){
					sourcePix = sSheet.getPixel(spriteX + x, spriteY + y);
					targetPix = tempImage.getPixel(startX + x, startY + y);

					//get source color values
					int r, g, b, a;
					r = sourcePix.getRed();
					g = sourcePix.getGreen();
					b = sourcePix.getBlue();
					a = sourcePix.getAlpha();

					//set alpha/opacity
					int targetAlpha = a;
					if(useOpacity){
						targetAlpha = (int)(a * opacity);
						// if((r+g+b) > 100) targetAlpha = (int)(targetAlpha * 2.0);
					}

					//set colors according to blend mode
					if((blendMode == 1) && targetAlpha > 0){
						//blend code based off of this: http://pastebin.com/vXc0yNRh
						
						double multiplier = 1.0 + (targetAlpha/255.0);//1.0 + (pixval/255.0)
						if(r+g+b < 50)	continue;
						// r += pixval;
						// g += pixval;
						// b += pixval;
						r = (int)(r * multiplier);
						g = (int)(g * multiplier);
						b = (int)(b * multiplier);
						int pixval = (r + g + b) / 3;
						if(useOpacity){
							targetAlpha = (int)(pixval * opacity);
							//r = (int)(r * multiplier);
							//g = (int)(g * multiplier);
							//b = (int)(b * multiplier);
							
							// if(r > 200 && g > 200 && b > 200 && opacity < 0.5){
							// 	targetAlpha /= 2;
							// 	r /= 2;
							// 	g /= 2;
							// 	b /= 2;
							// }
						}//else
							//targetAlpha = pixval;
						
						if(r > 255)	r = 255;
						if(g > 255)	g = 255;
						if(b > 255)	b = 255;
					}

					targetPix.setColor(new Color(r,g,b));
					targetPix.setAlpha(targetAlpha);
				}
			}//end for every pixel
			//partImage.write(FileChooser.getMediaDirectory() + "\\1stcopy.png");

			//apply rotation
			if(rotate != 0){
				rotateImage(rotate, tempImage);
				if(rotate == 90 || rotate == 270){
					newWidth = height;
					newHeight = width;
				}

				// partImage.write(FileChooser.getMediaDirectory() + "\\2rotate.png");
			}

			//apply flips to image; 0 = no flip, 1 = flip horiz, 2 = flip vert, 3 = flip both
			if(flip != 0){
				//manipulating whole image, so we need a new copy
				//tempImage = new Picture2(partImage.getWidth(), partImage.getHeight());
				for(y = 0; y < partImage.getHeight(); ++y){
					for(x = 0; x < partImage.getWidth(); ++x){
						// try{
							//get source pixel at current position
							sourcePix = tempImage.getPixel(x, y);
							
							//get target pixel position
							int targetX = 0;
							int targetY = 0;
							
							//horizontal flip
							if (flip == 1 || flip == 3)
								targetX += (partImage.getWidth() - 1 - x);
							else
								targetX += x;

							//vertical flip
							if (flip == 2 || flip == 3)
								targetY += (partImage.getHeight() - 1 - y);
							else
								targetY += y;
							
							//copy pixels at flipped coordinates
							targetPix = partImage.getPixel(targetX, targetY);
							targetPix.setColor(sourcePix.getColor());
							targetPix.setAlpha(sourcePix.getAlpha());
						// }catch(ArrayIndexOutOfBoundsException e){
						// 	continue;
						// }
					}//end y
				}//end x

				//copy it back
				tempImage.setAllPixelsToAnAlpha(0);
				tempImage.getGraphics().drawImage((BufferedImage) partImage.getImage(), 0, 0, null);
			}//else if(flip == 3){ //flip vertically and horizontally
			//	partImage = rotateImage(180, partImage);
			//}

			//if(flip != 0) partImage.write(FileChooser.getMediaDirectory() + "\\3flip.png");

			//prep variables for image shift
			int partStartX = centerX + frameX; //coords of top left of frame on image
			int partStartY = centerY + frameY + (finalFrameY/2) - lowestPoint;
			//"rotate" pixels if necessary
			if(rotate == 90 || rotate == 270){
				partStartX += width/2 - height/2;
				partStartY += height/2 - width/2;

				startX += width/2 - height/2;
				startY += height/2 - width/2;				
			}

			partImage.setAllPixelsToAnAlpha(0);
			//shift image over
			for(y = 0; y < height; ++y){
				for(x = 0; x < width; ++x){
					try{
						if(rotate == 90 || rotate == 270){//flipped x and y due to rotation
							sourcePix = tempImage.getPixel(startX + y,startY + x);
							targetPix = partImage.getPixel(partStartX + y + height/2,partStartY + x - width/2);
						}else{
							sourcePix = tempImage.getPixel(startX + x,startY + y);
							targetPix = partImage.getPixel(partStartX + x,partStartY + y);
						}
						targetPix.setColor(sourcePix.getColor());
						targetPix.setAlpha(sourcePix.getAlpha());
					}catch(ArrayIndexOutOfBoundsException e){
						continue;
					}
				}
			}
			partImage.write(partImageName);
			ProgramOutput.logMessage("Frame$Part.createPartImage: created " + FileManagement.getFilename(partImageName));
			// if(flip != 0) partImage.write(FileChooser.getMediaDirectory() + "\\4shift.png");
		}//end createPartImage

		//rotate an image about it's center
		private void rotateImage(int angle, Picture2 input){
			//based off of http://stackoverflow.com/questions/8639567/java-rotating-images
			//temporary copy of square image
			int w = input.getWidth();
			int h = input.getHeight();
			int max = (w > h) ? w : h; 
			int min = (w < h) ? w : h;
			int shiftX = (w == max) ? 0 : Math.abs(max-min)/2;
			int shiftY = (h == max) ? 0 : Math.abs(max-min)/2;
			Picture2 tempImage = new Picture2(max, max);
			for(int y = 0; y < h; ++y){
				for(int x = 0; x < w; ++x){
					Pixel sourcePix = input.getPixel(x,y);
					Pixel targetPix = tempImage.getPixel(shiftX + x, shiftY + y);
					targetPix.setColor(sourcePix.getColor());
					targetPix.setAlpha(sourcePix.getAlpha());
				}
			}
			// tempImage.write(FileChooser.getMediaDirectory() + "\\2rotate-norotate.png");

			//rotate/transform
			Picture2 newImage = new Picture2(max, max);
			double rotationRequired = Math.toRadians (angle);
			double locationX = (double)max/2;
			double locationY = (double)max/2;
			AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY);
			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);

			// Drawing the rotated image at the required drawing locations
			newImage.getGraphics().drawImage(op.filter((BufferedImage) tempImage.getImage(), null), 0, 0, null);
			// newImage.write(FileChooser.getMediaDirectory() + "\\2rotate-noshift.png");
		
			//resize to original dimensions
			//tempImage = null;
			//tempImage = newImage;
			//newImage = new Picture2(w,h);
			input.setAllPixelsToAnAlpha(0);
			for(int y = 0; y < h; ++y){
				for(int x = 0; x < w; ++x){
					Pixel sourcePix = newImage.getPixel(shiftX + x,shiftY + y);
					Pixel targetPix = input.getPixel(x, y);
					targetPix.setColor(sourcePix.getColor());
					targetPix.setAlpha(sourcePix.getAlpha());
				}
			}
			// newImage.write(FileChooser.getMediaDirectory() + "\\2rotate-func.png");

			//return newImage;
		}//end rotateImage

		public String getImage(){
			return partImageName;
		}
	}

	//constructor
	public Frame(int[][] cgg, int[][] cgs, int lineNumber, Picture2 sSheets[], String partPreName, boolean useOpacity, boolean makeParts, boolean saveParts){
		frameNumber = cgs[lineNumber][0];	//get frame number from cgs on cgg
		delay = (int)((cgs[lineNumber][3] / 60.0) * 1000);
		int[] frameLine = cgg[frameNumber];	//cgg has info for that numbered frame
		parts = new Part[frameLine[1]];		//initialize array of parts
		fileName = FileChooser.getMediaDirectory() + "\\test.png"; //can't use prename since frames will be saved in gif directory

		//create empty frame image
		if(makeParts){
			if(dimensions[0] == 0 || dimensions[1] == 0) setMaxDimensionsAndLP(cgs, cgg);
			int x = dimensions[0];
			int y = dimensions[1];
			image = new Picture2(x,y - (y/2 - lowestPoint));
			image.setAllPixelsToAColor(Unit.getTransparentColor());
			image.setAllPixelsToAnAlpha(0);
			//generate all the parts
			ProgramOutput.logMessage("Frame constructor: directory for all parts is :" + FileManagement.getDirectory(partPreName));
			for(int i = 0; i < parts.length; ++i){
				// System.out.println("Part " + i);
				parts[i] = new Part(frameLine, parts.length - 1 - i, partPreName);
				parts[i].createPartImage(sSheets,x,y,useOpacity);
				// parts[i].getImage().write(FileChooser.getMediaDirectory() + "\\part" + i + ".png");
			}

			for(Part p : parts){
				Picture2 partImage = new Picture2(p.getImage());
				copyTo(image, partImage, y/2 - lowestPoint);
			}
			
			if(!saveParts){
				deleteParts();
			}

			//further resize
			// Picture2 tempImage = image;
			// image = new Picture2(x, y - (y/2 - lowestPoint/2));
			// image.getGraphics().drawImage((BufferedImage) tempImage.getImage(), 0, 0, null);
		}

	}

	private void copyTo(Picture2 dest, Picture2 source, int diff){
		dest.getGraphics().drawImage((BufferedImage) source.getImage(), 
			0, 0, //dest x,y start
			dest.getWidth(), dest.getHeight(), //dest x,y end
			0,diff,//source start
			source.getWidth(), source.getHeight(),//source end
			null);
	}

	public static void resetDimensionsAndLP(){
		dimensions[0] = 0; dimensions[1] = 1;
		lowestPoint = -1;
	}
	
	public static int[] getLowestHighestFramePoints(Frame[] frames){
		int[] points = new int[2]; //0 - low, 1 - high
		int low = frames[0].getImage().getHeight() - 1;
		int high = 0;
		for(int i = 0; i < frames.length; ++i){
			Picture2 currFrame = frames[i].getImage();
			for(int y = 0; y < currFrame.getHeight(); ++y){
				for(int x = 0; x < currFrame.getWidth(); ++x){
					Pixel p = currFrame.getPixel(x, y);
					if(p.getAlpha() > 0){ //ie there's something in the pixel
						if(y > high)	high = y;
						if(y < low)		low = y;
					}
				}
			}//end for each pixel
		}//end for each frame
		points[0] = low;
		points[1] = high;
		return points;
	}
	
	public static int[] getLowestHighestFramePoints(Picture2 strip){
		int[] points = new int[2]; //0 - low, 1 - high
		int low = strip.getHeight() - 1;
		int high = 0;
		//no need to split by frame since it's one big image
		for(int y = 0; y < strip.getHeight(); ++y){
			for(int x = 0; x < strip.getWidth(); ++x){
				Pixel p = strip.getPixel(x, y);
				if(p.getAlpha() > 0){ //ie there's something in the pixel
					if(y > high)	high = y;
					if(y < low)		low = y;
				}
			}
		}//end for each pixel
		points[0] = low;
		points[1] = high;
		return points;
	}

	//get max dimensions of a given set of frames and the lowest y point in the frames
	public static void setMaxDimensionsAndLP(int[][] orderedFrames, int[][] cgg){
		int maxX = 65;	//default maxX from 0 is 140, 65 from center
		int maxY = 65; 	//default maxY from 0 is 140, 65 from center
		//boolean oddRotation = false;
		lowestPoint = -500;
		//for each frame
		for(int i = 0; i < orderedFrames.length; ++i){
			int currFrame = orderedFrames[i][0];
			int numParts = cgg[currFrame][1];
			//for each part in that frame
			for(int j = 0; j < numParts; ++j){
				//start coords and dimensions
				int currWidth = cgg[currFrame][10 + (j * 11)];
				int currX = cgg[currFrame][2 + (j * 11)];
				int currHeight = cgg[currFrame][11 + (j * 11)];
				int currY = cgg[currFrame][3 + (j * 11)];
				//int rotateAngle = cgg[currFrame][7 + (j * 11)];
				// if(rotateAngle != 0 || rotateAngle != 180)	oddRotation = true;

				// if(currWidth < 0) 	currWidth *= -1;
				// if(currHeight < 0) 	currHeight *= -1;

				if(Math.abs(currX) > maxX)	maxX = Math.abs(currX);
				if(Math.abs(currY) > maxY)	maxY = Math.abs(currY);

				//get distance from center at end of frame
				currX += currWidth;
				currY += currHeight;

				if(Math.abs(currX) > maxX)	maxX = Math.abs(currX);
				if(Math.abs(currY) > maxY)	maxY = Math.abs(currY);

				if(currY > lowestPoint)	lowestPoint = currY;
			}//end for each part
		}//end for each frame
		// System.out.println("Max = " + maxX + "x" + maxY);

		//add extra for any odd rotations
		//assume worse case rotation (90 or 270) of extra 50% of the height left and right
		//if(oddRotation){
		//	maxX += 1.5*maxY;
		//	lowestPoint += 1.5*lowestPoint;
		//}

		//round lowest point to next highest 10
		int diff = lowestPoint % 10;
		lowestPoint += 10;
		if(diff != 0)	lowestPoint += 	10-diff;

		//round maxX to nearest 10
		diff = maxX % 10;
		maxX += 10;
		if(diff != 0) maxX += 10-diff;

		dimensions[0] = maxX * 2;
		dimensions[1] = maxY * 2;

	}

	//get delay for this frame
	public int getDelay(){
		return delay;
	}

	//save the image; used for debugging
	public void save(){
		image.write(fileName);
		ProgramOutput.logMessage("Frame.save: created " + fileName);
	}

	//save parts in strip format
	public void saveParts(String fName){
		Picture2 currentPart = new Picture2(parts[0].getImage());
		Picture2 strip = new Picture2(currentPart.getWidth()*parts.length, currentPart.getHeight());
		int widthFrame = currentPart.getWidth();
		// int heightFrame = parts[0].getImage().getHeight();
		for(int i = 0; i < parts.length; ++i){
			currentPart = new Picture2(parts[i].getImage());
			int startX = widthFrame * i;
			// for(int y = 0; y < heightFrame; ++y){
			// 	for(int x = 0; x < widthFrame; ++x){
			// 		Pixel sourcePix = parts[i].getImage().getPixel(x,y);
			// 		Pixel targetPix = strip.getPixel(x + startX, y);

			// 		targetPix.setColor(sourcePix.getColor());
			// 		targetPix.setAlpha(sourcePix.getAlpha());
			// 	}
			// }//end for each pixel
			strip.getGraphics().drawImage((BufferedImage) currentPart.getImage(), startX, 0, null);
		}//end for each part
		strip.write(fName);
		ProgramOutput.logMessage("Frame.saveParts: created " + fName);
	}
	
	public void deleteParts(){
		ProgramOutput.logMessage("Frame constructor: directory for all parts is :" + FileManagement.getDirectory(parts[0].getImage()));
		for(int i = 0; i < parts.length; ++i){
			File currentPart = new File(parts[i].getImage());
			if(currentPart == null || !currentPart.delete()){
				ProgramOutput.printLoggedMessage(false, "Error in Frame.deleteParts: Failed to delete [" + parts[i].getImage() + "]");
			}else{
				ProgramOutput.logMessage("Frame.deleteParts: deleted " + FileManagement.getFilename(parts[i].getImage()));
			}
		}
		
	}

	public void setFileName(String f){
		fileName = f;
	}

	public String getFilename(){
		return fileName;
	}

	public Picture2 getImage(){
		return image;
	}

}
