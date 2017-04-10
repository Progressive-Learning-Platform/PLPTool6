package edu.asu.plp.tool.backend.plpisa.assembler;

import java.util.List;

import com.google.common.eventbus.Subscribe;

import edu.asu.plp.tool.backend.EventRegistry;
import edu.asu.plp.tool.backend.isa.ASMFile;
import edu.asu.plp.tool.backend.isa.ASMImage;
import edu.asu.plp.tool.backend.isa.Assembler;
import edu.asu.plp.tool.backend.isa.events.AssemblerControlEvent;
import edu.asu.plp.tool.backend.isa.events.AssemblerResultEvent;
import edu.asu.plp.tool.backend.isa.exceptions.AssemblerException;

public class PLPAssembler implements Assembler
{
	@Override
	public ASMImage assemble(List<ASMFile> asmFiles) throws AssemblerException
	{
		DisposablePLPAssembler assembler = new DisposablePLPAssembler(asmFiles);
		return assembler.assemble();
	}

	@Subscribe
	public void receivedAssembleRequest(AssemblerControlEvent e) {
		if (e.getCommand() == "assemble") {
			ASMImage image = null;
			try {
				image = assemble(e.getAssemblerFiles());
			} catch (AssemblerException e1) {
				EventRegistry.getGlobalRegistry().post(new AssemblerResultEvent(
						false, e1.getMessage(), e.getProjectName(), null));
			}
			EventRegistry.getGlobalRegistry().post(new AssemblerResultEvent(
						true, "", e.getProjectName(), image));
		}
	}

	@Override
	public void startListening() {
		EventRegistry.getGlobalRegistry().register(this);
	}
}
