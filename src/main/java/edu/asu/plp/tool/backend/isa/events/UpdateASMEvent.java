package edu.asu.plp.tool.backend.isa.events;

public class UpdateASMEvent {

	private String projectName;
	private String asmFileName;
	private String fileContent;
	public UpdateASMEvent(String projectName, String asmFileName, String fileContent) {
		this.projectName = projectName;
		this.asmFileName = asmFileName;
		this.fileContent = fileContent;
	}
	public String getProjectName() {
		return projectName;
	}
	public String getAsmFileName() {
		return asmFileName;
	}
	public String getFileContent() {
		return fileContent;
	}

}
