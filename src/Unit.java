/*
 *	Rewrite of BFFrameMaker project to better fit OOP principles.
 * 
 *	The Unit class contains all the information required for a unit.
 * 	Along with methods needed to set those values.
 *
 *	Started 12/2/2016
 * 
 *	@author Joshua Castor
 */

import java.awt.Color;

public class Unit {
	//same for all units
	private static Color transparentColor;	//color used for transparency in animation
	private static String dirGif;			//directory to store GIFs

	//different for each unit
	private String unitID;			//identifier
	private String dirUnit;			//directory of that specific unit
	private String cggFile;			//cgg file contains all frame info
	private String cgsFiles[];		//cgs files contain order of frames for specific animations
	private String sSheets[];
	private ARQueue errorList;

	/* constructors */
	public Unit(String id, String gifDir, String unitDir){
		transparentColor = new Color(253, 237, 43);
		dirGif = gifDir;
		
		errorList = new ARQueue();
		unitID = id;
		dirUnit = unitDir;
		cggFile = FileManagement.getSpecificFile(dirUnit, "cgg_" + unitID, ".csv");
		cgsFiles = FileManagement.getSpecificFiles(dirUnit, "cgs_" + unitID, ".csv");
		sSheets = FileManagement.getSpecificFiles(dirUnit, "anime_" + unitID, ".png");
		ProgramOutput.logMessage(this.toString());
	}

	/* methods */
	public static Color getTransparentColor(){
		return transparentColor;
	}

	public static String getDirGif(){
		return dirGif;
	}

	public String getID(){
		return unitID;
	}

	public String getDirUnit(){
		return dirUnit;
	}

	public String getCGG(){
		return cggFile;
	}

	public String[] getCGS(){
		return cgsFiles;
	}

	public String[] getSheets(){
		return sSheets;
	}

	public boolean isValidUnit(){
		return (cggFile != null && cgsFiles != null && sSheets != null);
	}

	public void addError(String s){
		errorList.enqueue("[" + unitID + "] - " + s);
	}

	public void printErrors(){
		while(!errorList.isEmpty()){
			ProgramOutput.printLoggedMessage(false, (String) errorList.dequeue());	
		}
	}

	public boolean noErrors(){
		return errorList.isEmpty();
	}
	
	public String toString(){
		String output = unitID + ":\n";
		output += "	dirUnit: " + dirUnit + "\n";
		output += "	dirGif: " + dirGif + "\n";
		if(cggFile != null)	output += " cgg: dirUnit\\" + FileManagement.getFilename(cggFile) + "\n";
		else				output += "	cgg: null\n";
		if(cgsFiles != null){
			output += " cgs: dirUnit\\" + FileManagement.getFilename(cgsFiles[0]);
			for(int i = 1; i < cgsFiles.length; ++i)
				output += ",dirUnit\\" + FileManagement.getFilename(cgsFiles[i]);
		}else{
			output += "	cgs: null";
		}

		if(sSheets != null){
			output += "\n	sSheets: dirUnit\\" + FileManagement.getFilename(sSheets[0]);
			for(int i = 1; i < sSheets.length; ++i)
				output += ",dirUnit\\" + FileManagement.getFilename(sSheets[i]);
		}else{
			output += "\n	sSheets: null";
		}
		output += "\n";
		return output;
	}

}
