
//import java.awt.*;
//import java.awt.font.*;
//import java.awt.geom.*;
import java.awt.image.BufferedImage;
//import java.text.*;
//import java.util.*;
//import java.util.List; // resolves problem with java.awt.List and java.util.List

/**
 * A class that represents a picture. This class inherits from SimplePicture2
 * and allows the student to add functionality to the Picture2 class.
 * 
 * Copyright Georgia Institute of Technology 2004-2005
 * 
 * @author Barbara Ericson ericson@cc.gatech.edu
 * 
 *         Modified on December 21, 2015 for PNG (use of alpha)
 */
public class Picture2 extends SimplePicture2 {
	///////////////////// constructors //////////////////////////////////

	/**
	 * Constructor that takes no arguments
	 */
	public Picture2() {
		/*
		 * not needed but use it to show students the implicit call to super()
		 * child constructors always call a parent constructor
		 */
		super();
	}

	/**
	 * Constructor that takes a file name and creates the picture
	 * 
	 * @param fileName
	 *            the name of the file to create the picture from
	 */
	public Picture2(String fileName) {
		// let the parent class handle this fileName
		super(fileName);
	}

	/**
	 * Constructor that takes the width and height
	 * 
	 * @param width
	 *            the width of the desired picture
	 * @param height
	 *            the height of the desired picture
	 */
	public Picture2(int width, int height) {
		// let the parent class handle this width and height
		super(width, height);
	}

	/**
	 * Constructor that takes a picture and creates a copy of that picture
	 */
	public Picture2(Picture2 copyPicture) {
		// let the parent class do the copy
		super(copyPicture);
	}

	/**
	 * Constructor that takes a buffered image
	 * 
	 * @param image
	 *            the buffered image to use
	 */
	public Picture2(BufferedImage image) {
		super(image);
	}

	////////////////////// methods ///////////////////////////////////////

	/**
	 * Method to return a string with information about this picture.
	 * 
	 * @return a string with information about the picture such as fileName,
	 *         height and width.
	 */
	public String toString() {
		String output = "Picture, filename " + getFileName() + " height " + getHeight() + " width " + getWidth();
		return output;

	}

	public static void main(String[] args) {
		// String fileName = FileChooser.pickAFile();
		// Picture2 pictObj = new Picture2(fileName);
		// pictObj.explore();
	}

} // this } is the end of class Picture2, put all new methods before this
