package edu.asu.plp.tool.backend.isa;

import java.util.List;

import javafx.util.Pair;


public interface ASMImage {
	List<Pair<ASMInstruction,ASMDisassembly>> getDisassemblyInfo();

}
