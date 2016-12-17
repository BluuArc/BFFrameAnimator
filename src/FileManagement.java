/*
 *	Rewrite of BFFrameMaker project to better fit OOP principles.
 * 
 *	Class to manage files.
 *
 *	Started 12/2/2016
 * 
 *	This program is licensed under the Creative Commons Attribution 3.0 United States License.
 *	Visit https://github.com/BluuArc/BFFrameAnimator for updates.
 *
 *	@author Joshua Castor
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileManagement{
	//method to count files in a file array
	public static int countFiles(File[] listFiles){
		int count = 0;
		for(File f : listFiles)
			if(f.isFile())
				count++;
		
		return count;
	}//end countFiles

	//method to get list of filenames in a path
	public static String[] getFilesInPath(String dir){
		String[] listString = new String[1];	//StringArray to output

		//adapted from http://stackoverflow.com/questions/1844688/read-all-files-in-a-folder
		
		//get filenames
		File folder = new File(dir);
		File[] listFiles = folder.listFiles();
		
		if(listFiles == null || listFiles.length == 0){
			System.out.println("Error in FileManagement.getFilesInPath: Directory [" + dir + "] is empty or not found.");
			return null;
		}
		
		//put filenames into string array (if there are any files
		int fileCount = countFiles(listFiles);
		if(fileCount != 0){
			int count = 0;
			listString = new String[fileCount];
			for(int i = 0; i < listFiles.length; ++i){
				if(listFiles[i].isFile()){
					listString[count] = listFiles[i].getName();
					count++;
				}
			}//end for
		}else{
			System.out.println("Error in FileManagement.getFilesInPath: Directory [" + dir + "] has directories, but no files in the root folder.");
			return null;
		}
		
		return listString;
	}//end getFilesInPath

	//get an array of filenames by (part of or full) name and extension
	//extension example: ".csv"
	public static String[] getSpecificFiles(String dir, String name, String extension){		
		//check name and extension parameters
		if(name.length() == 0 || extension.length() == 0){
			System.out.println("Error in FileManagement.getSpecificFiles: name and/or extension params cannot be empty");
			return null;
		}
		
		String[] output = null;
		String[] list = getFilesInPath(dir);	//get all files from path
		String[] temp = null;
		
		//check for empty directory
		if(getFilesInPath(dir) != null && list.length != 0)
			temp = new String[list.length];
		else{
			System.out.println("Error in FileManagement.getSpecificFiles: No files found");
			return null;
		}

		
		int count = 0;
		//get correct file names from directory
		for(int i = 0; i < list.length; ++i){
			temp[i] = null; //set everything else to null
			if(getFilename(list[i]).contains(name) && getFilename(list[i]).contains(extension)){
				temp[count] = list[i]; //store found into array
				count++;
			}
		}
		
		//check if any file was found
		if(count == 0){
			System.out.println("Error in FileManagement.getSpecificFiles: Files with name [" + name + "] and extension [" + extension +  "] not found in [" + dir + "]");
		}else{
			//copy temp array into resized output array
			output = new String[count];
			for(int i = 0; i < count; ++i){
				output[i] = dir + "\\" + temp[i];
			}
		}
		
		return output;
	}//end getFiles

	//method to get one file path from a directory by (part of or full) name and extension
	public static String getSpecificFile(String dir, String name, String extension){
		String[] files = getSpecificFiles(dir,name,extension);

		if(files != null)	return files[0];
		else				return null;
	}

	//get the file name of a file in a given path
	public static String getFilename(String fName) {
		int pos = fName.lastIndexOf(File.separatorChar);
		if(pos > 0)	fName = fName.substring(pos + 1, fName.length()); //it's a file path, otherwise it's a filename already
		return fName;
	}//end getFilename

	//get directory as a string
	public static String getDirectory(String fName) throws NullPointerException{
		int pos = fName.lastIndexOf(File.separatorChar);
		String dir = fName.substring(0, pos);
		return dir;
	}// end setDirectory method

	// set media path of file chooser class
	public static void setDirectory(String fName) {
		FileChooser.setMediaPath(fName);
	}// end setDirectory method

	//get number of lines in a file
	public static int getNumLines(String file){
		BufferedReader br = null;
		@SuppressWarnings("unused")
		String line = " ";
		int numLines = 0;

		try {
			//open file
			br = new BufferedReader(new FileReader(file));
			
			//while not at EOF, increment line counter
			while ((line = br.readLine()) != null) {
				numLines++;
			} // end while
		} catch (FileNotFoundException e) {
			System.out.println("Error in FileManagement.getNumLines: File [" + file + "] not found");
		} catch (IOException e) {
			System.out.println("Error in FileManagement.getNumLines: IO Exception when opening/reading file [" + file + "]");
		} finally {
			//close file
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					System.out.println("Error in FileManagement.getNumLines: IO Exception when closing file [" + file + "]");
				} // end catch
			} // end try
		} // end finally

		return numLines;
	}

	//store the lines of a file into a String array
	public static String[] getLines(String file){
		String[] output = new String[getNumLines(file)];
		BufferedReader br = null;
		String line = " ";
		int i = 0;

		try {
			//open file
			br = new BufferedReader(new FileReader(file));
			//while not at EOF
			while ((line = br.readLine()) != null) {
				// put each line into output array
				output[i++] = line;
			} // end while
		} catch (FileNotFoundException e) {
			System.out.println("Error in FileManagement.getLines: File [" + file + "] not found");
		} catch (IOException e) {
			System.out.println("Error in FileManagement.getLines: IO Exception when opening/reading file [" + file + "]");
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					System.out.println("Error in FileManagement.getLines: IO Exception when closing file [" + file + "]");
				} // end catch
			} // end try
		} // end finally

		return output;
	}

	// method to convert string array to integer array
	public static int[] convertToInt(String[] input) {
		int[] frame = new int[input.length];
		for (int i = 0; i < input.length; i++)
			frame[i] = Integer.parseInt(input[i]);
		return frame;
	}// end convertToInt method
}