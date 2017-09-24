package edu.asu.SimulatorFiles;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;

import edu.asu.plp.tool.backend.plpisa.sim.PLPSimulator;

import java.io.*;


public class ParseTxtFile {
	static String path = "/Users/Abhilash/Downloads/DisassemblyOfFile.txt";
	static DummySim sim = new DummySim();
	
	public static void mainfunc() throws IOException {
		
		System.out.println("In parse text file..");
				
		String content = new String();
		content = readFile(path);
		String str1, str2;
		long val1, val2;
		
		
		content = content.replaceAll("\\s+", "");
		//System.out.println(content);
		
		String[] arrContent = content.split("\\|");
		
		for(int i=0; i<arrContent.length; i++){
			if(arrContent[i].startsWith("0x")){
				str1 = arrContent[i+1];
				str2 = arrContent[i+3];
				
				val1 = Long.parseLong(str1);
				val2 = Long.parseLong(str2);
				
				System.out.println("Address[Dec]: " + val1  + " InstructionEncoding[Dec]: " + val2);
				boolean res = sim.stepFunctional(val2);
				

				i=i+3;
			}
		}
		
	}
	
	
	static String readFile(String fileName) throws IOException {
	    BufferedReader br = new BufferedReader(new FileReader(fileName));
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            line = br.readLine();
	        }
	        return sb.toString();
	    } finally {
	        br.close();
	    }
	}

	
	
}
