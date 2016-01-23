package edu.asu.plp.tool.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import edu.asu.plp.tool.backend.isa.Simulator;
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
		Function<String, Boolean> supportsProjectType;
		supportsProjectType = (type) -> type.toLowerCase().startsWith("plp");
		Simulator simulator = new PLPSimulator();
		ISAModule plp6Module = new ISAModule(null, simulator, supportsProjectType);
		registeredModules.add(plp6Module);
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
