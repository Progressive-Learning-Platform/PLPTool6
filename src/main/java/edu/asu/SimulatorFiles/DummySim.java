package edu.asu.SimulatorFiles;



import java.net.*;
import java.io.*;

public class DummySim {
	
	private ALU alu = new ALU();
	private ProgramCounter programCounter = new ProgramCounter(0);

	private boolean isBranched;
	private long branchDestination;
	private PLPRegFile regFile = new PLPRegFile();
	

 boolean stepFunctional(long instruction)
	{
		System.out.println("In dummy ism stepfunctonal");
		
		long pcplus4 = programCounter.evaluate() + 4;

		//decode instruction
		int opcode = InstructionExtractor.opcode(instruction);
		byte rs = InstructionExtractor.rs(instruction);
		byte rd = InstructionExtractor.rd(instruction);
		byte rt = InstructionExtractor.rt(instruction);
		byte funct = InstructionExtractor.funct(instruction);
		long imm = InstructionExtractor.imm(instruction);
		long jaddr = InstructionExtractor.jaddr(instruction);
		
		//TODO memory read
		long s = regFile.read(rs);	//0; //regFile.read(rs)
		long t = regFile.read(rt);	//0; //regFile.read(rt)
		long s_imm = (short) imm & 0xffffffffL;
		long alu_result;
		
		//execute
		if(opcode == 0)
		{
			if(funct == 0x08 || funct == 0x09) //JR
			{
				isBranched = true;
				branchDestination = s;
				
				if(funct  == 0x09) //jalr
				{
					//TODO memory write
					//regFile.write(rd, (int)pcplus4 + 4, false);
					
					System.out.println("in:-- funct  == 0x09 line 45" );
					//regFile.write(rd, pcplus4 + 4, false);
				}
			}
			else
			{
				alu_result = alu.evaluate(s, t, instruction);
				alu_result &= 0xffffffffL;
				
				//TODO memory write
				//regFile.write(rd, (int)alu_result, false);
				System.out.println("in else block.. line 57");
				//regFile.write(rd, alu_result, false);
			}
		}
		else if(opcode == 0x04) //beq
		{
			if(s == t)
			{
				isBranched = true;
				branchDestination = (pcplus4 + (s_imm << 2)) & 0xffffffffL;
			}
		}
		else if(opcode == 0x05) //bne
		{
			if(s != t)
			{
				isBranched = true;
				branchDestination = (pcplus4 + (s_imm << 2)) & 0xffffffffL;
			}
		}
		else if(opcode == 0x23) //lw
		{
			//TODO bus read
			//Long data = (Long) 0L; //bus.read((s + s_imm) & 0xffffffffL)
			Long data = 0L; //(Long)addressBus.read((s + s_imm) & 0xffffffffL);
			//Integer data = 0;
			if(data == null)
			{
				System.out.println("Bus read error");
				return false;
			}
			
			//TODO memory write
			//regFile.write(rt, data, false);
			//regFile.write(rt, data.intValue(), false);
			
			System.out.println("line 99");
		}
		else if(opcode == 0x2B) //sw
		{
			//TODO bus write
			//int ret = bus.write((s + s_imm) & 0xffffffffL, regFile.read(rt), false);
			int ret =    0; ////addressBus.write((s + s_imm) & 0xffffffffL, regFile.read(rt), false);
			
			if(ret > 0)
			{
				System.out.println("Bus write error");
				return false;
			}
		}
		else if(opcode == 0x02 || opcode == 0x03) // j
		{
			isBranched = true;
			branchDestination = jaddr << 2 | (pcplus4 & 0xf0000000L);
			
			if(opcode == 0x03) //jal
			{
				//TODO memory write
				//regFile.write(31, pcplus4 + 4, false);
				//regFile.write(31,  (int)pcplus4 + 4, false);
				
				System.out.println("in if opcode == 0x03 line 115");
			}
		}
		else if(opcode == 0x0C || opcode == 0x0D) //ori, andi
		{
			alu_result = alu.evaluate(s, imm, instruction) & 0xffffffffL;
			//TODO memory write
			//regFile.write(rt, alu_result, false);
			//regFile.write(rt, (int)alu_result, false);
			
			System.out.println("in elseif line 125");
		}
		else
		{
			alu_result = alu.evaluate(s, s_imm, instruction);
			
			if(alu_result == -1)
			{
				System.out.println("Unhandled instruction: invalid op-code");
				return false;
			}
			
			alu_result &= 0xffffffffL;
			//TODO memory write
			//regFile.write(rt, alu_result, false);
			// regFile.write(rt, (int)alu_result, false);
			
			System.out.println("line 149");
		}
		
		//TODO Bus actions
		//bus.eval();
		//addressBus.eval();
		
		//Evaluate interrupt controller again to see if anything raised an irq
		//(PLP sim bus evaluates modules from index 0 upwards)
		//bus.eval(0);
		//addressBus.eval(0);
		
		//We have an irq waiting, set ack so the controller wont set another
		//request while we process this one
		
		return true;
	}

	
}
