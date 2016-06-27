
public class Debug {

	public static void main(String[] args) {
		String dir = FileChooser.pickAFile();
		dir = BFFrameMaker.getDirectory(dir);
		
		String[] temp = BFFrameMaker.getCGSFiles(dir);
		
		for(int i = 0; i < temp.length; ++i)
			System.out.println(temp[i]);
		return;

	}

}
