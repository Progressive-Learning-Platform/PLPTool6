package edu.asu.plp.tool.backend.plpisa.assembler2;

public class PLPDisassemblyInfo 
{
	public int getLineNumber() {
		return nLineNumber;
	}

	public int getAddress() {
		return nAddress;
	}

	public int getEncodedValue() {
		return nEncodedValue;
	}

	public String getActualInstruction() {
		return strActualInstruction;
	}

	public String getSubInstruction() {
		return strSubInstruction;
	}

	public String getSourceFile() {
		return strSourceFile;
	}

	private int nLineNumber;
	private int nAddress;
	private int nEncodedValue;
	private String strActualInstruction;
	private String strSubInstruction;
	private String strSourceFile;
	
	public PLPDisassemblyInfo(int nline, long nAddress, long nEncodedValue, String acInst, String suInst, String source)
	{
		this.nLineNumber = nline;
		this.nAddress = (int)nAddress;
		this.strActualInstruction = acInst;
		this.nEncodedValue = (int)nEncodedValue;
		this.strSubInstruction = suInst;
		this.strSourceFile = source;
	}
	
	
	public String toString()
	{
		String strDisasm = "";
		
		strDisasm = String.format("0x%-10x | %12d | 0x%-22x | %24d | %-40s | %-40s | %11d | %-40s", nAddress, nAddress & 0xFFFFFFFFL, nEncodedValue, nEncodedValue & 0xFFFFFFFFL, strActualInstruction, strSubInstruction, nLineNumber, strSourceFile);
		
		//strDisasm = Integer.toHexString(nAddress) + "\t" + Integer.toString(nAddress) + "\t" + Integer.toHexString(nEncodedValue)+ "\t" + Integer.toString(nEncodedValue) + "\t" +this.strActualInstruction + "\t" + this.strSubInstruction + "\t" + Integer.toString(nLineNumber) +"\t"+ this.strSourceFile;
		
		return strDisasm;
		
	}
	
	

}
