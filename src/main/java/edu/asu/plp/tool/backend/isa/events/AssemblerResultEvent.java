package edu.asu.plp.tool.backend.isa.events;

import edu.asu.plp.tool.backend.isa.ASMImage;

/**
 ** This class is controls the assembler and manages the result
 **/
public class AssemblerResultEvent {

	private boolean assembleSuccess;
	private String errorMessage;
	private String projectName;
	private ASMImage asmImage;

	/**
	 * This is a constructor for which accepts Success, ErrorMessage, ProjectName and ASMImage parameters
	 * @param {String} Success        - It takes the assembler success parameter
	 * @param {String} ErrorMessage   - It takes the error message parameter
	 * @param {string} ProjectName    - It takes the name of the project
	 * @param {string} ASMImage       - It takes the ASM Image
	 * **/

	public AssemblerResultEvent(boolean assembleSuccess, String errorMessage,
			String projectName, ASMImage asmImage) {
		this.assembleSuccess = assembleSuccess;
		this.errorMessage = errorMessage;
		this.projectName = projectName;
		this.asmImage = asmImage;
	}

	/**
	 ** This is a function which return assemble success
	 **/
	public boolean getAssembleSuccess() {
		return assembleSuccess;
	}

    /**
     ** This is a function which return error message to the screen
     **/
	public String getErrorMessage() {
		return errorMessage;
	}

    /**
     ** This is a function which return the project name
     **/
	public String getProjectName() {
		return projectName;
	}

    /**
     ** This is a function which return ASM Image
     **/
	public ASMImage getAsmImage() {
		return asmImage;
	}

}
