package edu.asu.plp.tool.backend.util;

import java.util.Scanner;
import java.io.File;
import java.io.IOException;

import edu.asu.plp.tool.prototype.model.PLPProject;

public class PLP5Porting {
	private static String absPath;
	private static PLPProject project;
	
	public static PLPProject loadProject(File projectFile)throws IOException
	{
		PLP5ProjectParser parser = new PLP5ProjectParser();
		return parser.parse(projectFile);
	}
	
	public static void traverseDirectory(File directory) throws IOException{
		for (File subEntry : directory.listFiles()){
			if (!subEntry.isDirectory()){
				project = loadProject(subEntry);
				project.saveLegacy();
			}
			else{
				traverseDirectory(subEntry);
			}
		}
	}
	
	public static void main(String args[])
	{
		try{
			System.out.println("Enter the absolute path:");
			Scanner input = new Scanner(System.in);
			absPath = input.nextLine();
			File f = new File(absPath);
			if(!f.exists() || !f.isDirectory())
				System.out.println("Invalid path or directory\n");
			
			traverseDirectory(f);
			input.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
