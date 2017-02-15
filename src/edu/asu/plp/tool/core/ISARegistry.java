package edu.asu.plp.tool.core;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import edu.asu.plp.tool.backend.isa.Assembler;
import edu.asu.plp.tool.backend.isa.Simulator;
//import edu.asu.plp.tool.backend.plpisa.assembler.PLPAssembler;
import edu.asu.plp.tool.backend.plpisa.assembler2.PLPAssembler;
import edu.asu.plp.tool.backend.plpisa.sim.PLPSimulator;

public class ISARegistry
{
	private static ISARegistry globalInstance;
	private List<ISAModule> registeredModules;
	
	public static ISARegistry getGlobalRegistry()
	{
		initialize();
		return globalInstance;
	}
	
	/**
	 * Shorthand for {@link #getGlobalRegistry()}
	 */
	public static ISARegistry get()
	{
		return getGlobalRegistry();
	}
	
	public static void initialize()
	{
		if (globalInstance == null)
			globalInstance = new ISARegistry();
	}
	
	/**
	 * Private constructor to enforce singleton
	 */
	private ISARegistry()
	{
		registeredModules = new ArrayList<>();
		// TODO: Find and load all ISAModules
		// ISA Modules will be found on the disk, in the PLP directory located in either
		// the "mods" or "isas" sub-directories.
		// The modules will be stored in a jar
		// They will be compiled as a class of type <? extends ISAModule>
		
		// TODO: The following is temporary
		// TODO: Remove and replace with a decoupled solution
		
		//Did some work of what is mentioned in TODO - Harsha
		File folder = new File("isa");
		if(folder.exists() && folder.isDirectory())
		{
			File[] listOfFiles = folder.listFiles();
			if(listOfFiles.length > 0)
			{
				try
				{
					for(File jarFile: listOfFiles)
					{
						URLClassLoader cl = URLClassLoader.newInstance(new URL[]{jarFile.toURI().toURL()});
						String architec = jarFile.getName().substring(0, jarFile.getName().indexOf("_isa.jar"));
						Class jarClass = cl.loadClass("edu.asu.plp.tool.backend."+architec+"isa.ModuleObjectCreator");
						Method getMod = jarClass.getMethod("getModule");
						registeredModules.add((ISAModule)getMod.invoke(null));
						
					}
				}
				catch(Exception exp)
				{
					exp.printStackTrace();
				}
			
			}
			else
			{
				Function<String, Boolean> supportsProjectType;
				supportsProjectType = (type) -> type.toLowerCase().startsWith("plp");
				Simulator simulator = new PLPSimulator();
				Assembler assembler = new PLPAssembler();
				ISAModule plp6Module = new ISAModule(assembler, simulator, supportsProjectType);
				registeredModules.add(plp6Module);
			}
		}
		else
		{
			Function<String, Boolean> supportsProjectType;
			supportsProjectType = (type) -> type.toLowerCase().startsWith("plp");
			Simulator simulator = new PLPSimulator();
			Assembler assembler = new PLPAssembler();
			ISAModule plp6Module = new ISAModule(assembler, simulator, supportsProjectType);
			registeredModules.add(plp6Module);
		}
	}
	
	public Optional<ISAModule> lookupByProjectType(String projectType)
	{
		for (ISAModule module : registeredModules)
		{
			if (module.supportsProjectType(projectType))
				return Optional.of(module);
		}
		
		return Optional.empty();
	}
}
