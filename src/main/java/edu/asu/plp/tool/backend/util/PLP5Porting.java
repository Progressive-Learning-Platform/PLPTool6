package edu.asu.plp.tool.backend.util;

import java.util.Scanner;
import java.io.File;
import java.io.IOException;

import edu.asu.plp.tool.prototype.model.PLPProject;

public class PLP5Porting {
	private static String absPath;
	
	public static void loadProject(PLPProject project, File projectFile)throws IOException
	{
		PLP5ProjectParser parser = new PLP5ProjectParser();
		parser.parse(project, projectFile);
	}
	
	public static void traverseDirectory(PLPProject project, File directory) throws IOException{
		for (File subEntry : directory.listFiles()){
			if (!subEntry.isDirectory()){
				loadProject(project, subEntry);
				project.saveLegacy();
			}
			else{
				traverseDirectory(project, subEntry);
			}
		}
	}
	
	public static void main(String args[])
	{
		PLPProject project = new PLPProject();
		try{
			System.out.println("Enter the absolute path:");
			Scanner input = new Scanner(System.in);
			absPath = input.nextLine();
			File f = new File(absPath);
			if(!f.exists() || !f.isDirectory())
				System.out.println("Invalid path or directory\n");
			
			traverseDirectory(project, f);
			input.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
