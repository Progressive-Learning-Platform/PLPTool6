package edu.asu.plp.tool.backend.isa.events;

import edu.asu.plp.tool.backend.isa.ASMImage;

public class AssemblerResultEvent {

	private boolean assembleSuccess;
	private String errorMessage;
	private String projectName;
	private ASMImage asmImage;
	
	public AssemblerResultEvent(boolean assembleSuccess, String errorMessage,
			String projectName, ASMImage asmImage) {
		this.assembleSuccess = assembleSuccess;
		this.errorMessage = errorMessage;
		this.projectName = projectName;
		this.asmImage = asmImage;
	}

	public boolean getAssembleSuccess() {
		return assembleSuccess;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
	
	public String getProjectName() {
		return projectName;
	}

	public ASMImage getAsmImage() {
		return asmImage;
	}

}
