package edu.asu.plp.tool.backend.isa.events;

import edu.asu.plp.tool.backend.isa.ASMImage;

/**
 * This class is controls the assembler and manages the result
 */
public class AssemblerResultEvent {

	private boolean assembleSuccess;
	private String errorMessage;
	private String projectName;
	private ASMImage asmImage;

	/**
	 *
	 * @param assembleSuccess	It takes the assembler success parameter. If the value is true then the program
								assembled successfully otherwise not
	 * @param errorMessage		It takes the error message parameter if any error is generated while assembling
	 							the result
	 * @param projectName		It takes the name of the project for which the assembler result is generated
	 * @param asmImage			It takes the ASM Image of the assembled class
	 */
	public AssemblerResultEvent(boolean assembleSuccess, String errorMessage,
			String projectName, ASMImage asmImage) {
		this.assembleSuccess = assembleSuccess;
		this.errorMessage = errorMessage;
		this.projectName = projectName;
		this.asmImage = asmImage;
	}

	/**
	 *
	 * @return assemble success or not
	 */
	public boolean getAssembleSuccess() {
		return assembleSuccess;
	}

	/**
	 *
	 * @return error message to the screen
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 *
	 * @return the name of the project
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 *
	 * @return the ASM image
	 */
	public ASMImage getAsmImage() {
		return asmImage;
	}

}
