package edu.asu.plp.tool.backend.plpisa.sim;
import plptool.Constants;

import java.util.ArrayList;

public class BreakpointModule {
	private ArrayList<Breakpoint> breakpoints;

    /**
     * The Breakpoint constructor creates a new arraylist of breakpoints
     */
    public BreakpointModule() {
        breakpoints = new ArrayList<Breakpoint>();
    }

    /**
     * Add a Breakpoint to the simulation core
     *
     * @param addr The address of the program to break on
     * @param fileMap File mapper for GUI purposes
     * @param lineNumMap File number mapper for GUI purposes
     */
    public void add(long addr, int fileMap, int lineNumMap) {
        breakpoints.add(new Breakpoint(addr, fileMap, lineNumMap));
    }

    /**
     * Remove a Breakpoint specified by the address
     *
     * @param addr The address of the Breakpoint to remove
     * @return True if the Breakpoint exists, false otherwise
     */
    public boolean remove(long addr) {
        boolean ret = false;

        for(int i = 0; i < breakpoints.size(); i++) {
            if((long) breakpoints.get(i).getAddr() == addr) {
                breakpoints.remove(i);
                ret = true;
            }
        }

        return ret;
    }

    /**
     * Clear breakpoints
     */
    public void clear() {
        breakpoints = new ArrayList<Breakpoint>();
    }

    /**
     * Return whether the simulation core has any Breakpoint installed
     *
     * @return True if the simulation core has breakpoints, false otherwise
     */
    public boolean hasBreakpoint() {
        return breakpoints.size() > 0 ? true : false;
    }

    /**
     * Check if the specified address is a Breakpoint
     *
     * @param addr Address to check
     * @return True if the address is a Breakpoint, false otherwise
     */
    public boolean isBreakpoint(long addr) {
        for(int i = 0; i < breakpoints.size(); i++) {
            if((long) breakpoints.get(i).getAddr() == addr) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if the specified file and its line number is a Breakpoint
     *
     * @param fIndex The file mapper index for the file in question
     * @param lineNum The line number of the code
     * @return True if the location is a Breakpoint, false otherwise
     */
    public boolean isBreakpoint(int fIndex, int lineNum) {
        for(int i = 0; i < breakpoints.size(); i++) {
            if((breakpoints.get(i).getFileMap() == fIndex) &&
               (breakpoints.get(i).getLineNumMap() == lineNum)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Return the number of breakpoints installed on this simulation core
     *
     * @return Number of breakpoints
     */
    public int size() {
        return breakpoints.size();
    }

    /**
     * Get the breakpoint specified by index
     *
     * @param index Index of the breakpoint
     * @return Breakpoint reference, or null if the index is out of bounds
     */
    public long getBreakpointAddress(int index) {
        if(index < 0 || index > breakpoints.size())
            return Constants.PLP_NUMBER_ERROR;

        return breakpoints.get(index).getAddr();
    }

    /**
     * Remove breakpoint specified by index
     *
     * @param index Index of breakpoint to be removed
     * @return boolean True if successful, false otherwise
     */
    public boolean removeBreakpoint(int index) {
        if(index < 0 || index > breakpoints.size())
            return false;

        breakpoints.remove(index);
        return true;
    }
}



