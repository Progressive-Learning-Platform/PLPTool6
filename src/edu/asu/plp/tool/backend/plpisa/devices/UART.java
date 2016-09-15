package edu.asu.plp.tool.backend.plpisa.devices;

import java.util.LinkedList;
import java.util.Queue;

import edu.asu.plp.tool.backend.plpisa.sim.PLPSimulator;
import plptool.Constants;

/**
 * This class is the working model of UART in our PLPTool.
 * It has 4 register
 * startAddress - CommandRegister
 * startAddress + 4 - Status Register
 * startAddress + 8 - Receive Register
 * startAddress + 12 - Send Register
 * It sets interrupt based on status register. In our case it is ready variable.
 * @author Harsha
 *
 */
public class UART extends PLPIOMemoryModule {
	/**
	 * This is the local receive buffer used by the UART
	 */
	private long receiveBuffer;
	
	/**
	 * This is the local send buffer used by the UART
	 */
	private long sendBuffer;
	
	/**
	 * This will help to perform the actions of status register
	 */
	private boolean ready = false;
	
	/**
	 * This is our reference to simulator which is like processor. 
	 * We have this to initiate interrupts on UART ready or not
	 */
	private PLPSimulator sim;
	
	/**
	 * As user types contents, those data will get stored here. Only first character is displayed to user via receive buffer
	 */
	private Queue<Long> internalBuffer = new LinkedList<Long>();
	
	/**
	 * Constructor for our UART class. It might be missing the UART GUI part
	 * @param address starting address that is command registers address
	 * @param sim reference to PLPSimulator
	 */
	public UART(long address, PLPSimulator sim)
	{
		super(address, address+12, true);
		this.sim = sim;
		//TODO: Do we need to have strong coupling of UI Frame of UART??
	}

	/**
	 * This is the function run by the processor for a clock cycle to to the UART operations.
	 */
	@Override
	public int eval() {
		if(!ready && internalBuffer.size() != 0)
		{
			ready = true;
			receiveBuffer = (Long)internalBuffer.remove();
		}
		
		//TODO: Put these IRQ values in the configuration and use config variables
		if(ready)
			sim.setIRQ(0x4L);
		else
			sim.maskIRQ(0xfffffffbL);
		
		return Constants.PLP_OK;
	}

	/**
	 * This is the function run by the processor for a clock cycle to to the UART operations.
	 */
	@Override
	public int gui_eval(Object x) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * This function will will just erase all the data present in the buffers/registers of UART. To restart UART
	 */
	@Override
	public void reset() {
		ready = false;
		internalBuffer = new LinkedList<Long>();
		super.writeReg(startAddress+4, new Long(0L), false);
		super.writeReg(startAddress+8, new Long(0L), false);
		super.writeReg(startAddress+12, new Long(0L), false);
		
	}

	/**
	 * This describes this device.
	 */
	@Override
	public String introduce() {
		return "UART";
	}
	
	/**
	 * This function is used read the contents of the UART registers.
	 */
	@Override
	public Object read(long addr)
	{
		Long ret = new Long(0);
		
		if(addr == startAddress)
		{
			//Reading Command register. As of now it returns 0 as 
			// it is a write only register
			return ret;
		}
		else if(addr == startAddress+4)
		{
			// Reading Status register
			if(ready)
			{
				//returning b11
				ret = new Long(3);
			}
			else
			{
				//return b01
				ret = new Long(1);
			}
		}
		else if(addr == startAddress+8)
		{
			// Reading receive Buffer
			ret = new Long(receiveBuffer);
		}
		else if(addr == startAddress+12)
		{
			// Reading send buffer
			ret = new Long(sendBuffer);
		}
		else
		{
			//TODO error throw an exception
		}
		
		return ret;
	}
	
	/**
	 * This function writes to the UART registers.
	 * We dont actually have command  so writing to them lead to 
	 * some actions in other places. 
	 */
	@Override
	public int write(long addr, Object input, boolean isInstr)
	{
		long data = (Long)input;
		
		if(addr == startAddress)
		{
			//Writing a command buffer
			if(data == 1)
			{
				//TODO:send the contents of send buffer to UI
			}
			else if(data == 2)
			{
				ready = false;
			}
			else
			{
				//TODO: invalid input throw an exception
			}
		}
		else if(addr == startAddress+4)
		{
			//TODO: trying to write to status buffer throw an exception
		}
		else if(addr == startAddress+8)
		{
			//TODO: Throw an error as trying to write to receive buffer
		}
		else if(addr == startAddress+12)
		{
			sendBuffer = data & 0x000000ff;
		}
		else
		{
			//TODO: Throw an error as it is an invalid register address for UART
		}
		
		return Constants.PLP_OK;
	}

}
