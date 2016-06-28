
public class Debug {

	public static void main(String[] args) {
		String dir = FileChooser.pickAFile();
		dir = BFFrameMaker.getDirectory(dir);
		
		
		String[] temp = BFFrameMaker.getFiles(dir, "cgs", ".csv");
		
		if(temp != null)
			for(int i = 0; i < temp.length; ++i)
				System.out.println(temp[i]);	
		
		System.out.println(BFFrameMaker.getFile(dir, "anime", ".png"));
		System.out.println(BFFrameMaker.getFile(dir, "cgg", ".csv"));
		System.out.println(BFFrameMaker.getFile(dir, "cgs", ".csv"));
		
		temp = BFFrameMaker.getFiles(dir, "cgg", ".csv");
		
		if(temp != null)
			for(int i = 0; i < temp.length; ++i)
				System.out.println(temp[i]);
		
		return;
	}

}
