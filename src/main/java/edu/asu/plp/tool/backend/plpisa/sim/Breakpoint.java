package edu.asu.plp.tool.backend.plpisa.sim;

public class Breakpoint {
	private long addr;
    private int fileMap;
    private int lineNumMap;

    public Breakpoint(long addr, int fileMap, int lineNumMap) {
        this.addr = addr;
        this.fileMap = fileMap;
        this.lineNumMap = lineNumMap;
    }

    public long getAddr() {
        return addr;
    }

    public int getFileMap() {
        return fileMap;
    }

    public int getLineNumMap() {
        return lineNumMap;
    }
}

