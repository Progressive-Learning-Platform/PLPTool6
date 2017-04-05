package edu.asu.plp.tool.backend.mipsisa.sim;

import edu.asu.plp.tool.backend.isa.exceptions.SimulatorException;
import edu.asu.plp.tool.backend.mipsisa.InstructionExtractor;

public class ALU
{
	// 32-bit overflow mask
	private static long ARITHMETIC_OVERFLOW_VALUE = 0x7fffffff;
	
	public ALU()
	{
	}
	
	public long evaluate(long a, long b, long instruction) throws SimulatorException
	{
		//@formatter:off
		switch (InstructionExtractor.opcode(instruction))
		{
			case 0: // R-types
				switch (InstructionExtractor.funct(instruction))
				{
					case 0x24: return a & b;
					case 0x25: return a | b;
					case 0x26: return a ^ b;
					case 0x27: return ~(a | b);
					case 0x20: 
						//OVERFLOW HERE if a+b overflows
						long temp = a + b;
						// if temp is +, compare to largest value
						// OR
						// if temp is -, negate and compare to largest value + 1
						if (temp > ARITHMETIC_OVERFLOW_VALUE || -temp > ARITHMETIC_OVERFLOW_VALUE + 1)
						{
							throw new SimulatorException("Arithematic Overflow");
						}
						return temp;
					case 0x21: return a + b;
					case 0x22:
						long tempSub = a - b;
						if (tempSub > ARITHMETIC_OVERFLOW_VALUE || -tempSub > ARITHMETIC_OVERFLOW_VALUE + 1)
						{
							throw new SimulatorException("Arithematic Overflow");
						}
						return tempSub;
					case 0x23: return a - b;
					case 0x2A:
						int aSigned = (int) a;
						int bSigned = (int) b;
						return (aSigned < bSigned) ? 1 : 0;
					case 0x2B: return (a < b) ? 1 : 0;
					case 0x00: 
						return b << InstructionExtractor.sa(instruction);
					case 0x02:
						if (a == 1) { //rotr
							int shamt = InstructionExtractor.sa(instruction);
							long temp1 = b >>> shamt;
							long temp2 = b << (32 - shamt);
							temp2 &= 0xffffffffL;
							return temp1 | temp2;
						} else { //srl
							return b >>> InstructionExtractor.sa(instruction);
						}
					case 0x03: return b >> InstructionExtractor.sa(instruction);
					case 0x07: return a >> b;
					case 0x10:
						return ((long)(int) a * (long)(int)b) & 0xffffffffL;
					case 0x11:
						return (((long)(int) a * (long)(int)b) & 0xffffffff00000000L) >> 32;
					case 0x04: return a << b;
                    case 0x06: 
                    	if (InstructionExtractor.sa(instruction) == 1) { //rotrv
							long temp1 = a >>> b;
							long temp2 = a << (32 - b);
							temp2 &= 0xffffffffL;
							return temp1 | temp2;
                    	} else { //srlv
                    		return a >>> b; //because it's logical
                    	}
					case 0x18:
					case 0x19: return a * b;
					case 0x1A:
					case 0x1B: 
						long value = a % b;
						value = value << 32;
						return (value & 0xffffffff00000000L) | (a / b & 0x00000000ffffffffL);
				}
			case 0x04: return (a - b == 0) ? 1 : 0;
            case 0x05: return (a - b == 0) ? 0 : 1;
            case 0x0c: return a & b;
            case 0x0d: return a | b;
            case 0x0e: return a ^ b;
            case 0x0f: return b << 16;
            case 0x0A:
                int aSigned = (int) a;
                int bSigned = (int) b;
                return (aSigned < bSigned) ? 1 : 0;
            case 0x0B: return (a < b) ? 1 : 0;
            case 0x09:
            case 0x23:
            case 0x2B: return a + b;
            case 0x08:
            	long temp = a + b;
				if (temp > ARITHMETIC_OVERFLOW_VALUE || -temp > ARITHMETIC_OVERFLOW_VALUE + 1)
				{
					throw new SimulatorException("Arithematic Overflow");
				}
				return temp;
            case 0x1C:
            	switch (InstructionExtractor.funct(instruction)) {
            		case 0x20:
            			int n = 0;
            		    if (a <= 0x0000ffff) { n += 16; a <<= 16; }
            		    if (a <= 0x00ffffff) { n +=  8; a <<= 8; }
            		    if (a <= 0x0fffffff) { n +=  4; a <<= 4; }
            		    if (a <= 0x3fffffff) { n +=  2; a <<= 2; }
            		    if (a <= 0x7fffffff) n ++;
            		    return n;
            		case 0x21:
            			int n1 = 0;
            			
            			if (a >= 0xffff0000) { n1 += 16; a >>= 16; }
            			if (a >= 0xff000000) { n1 +=  8; a >>= 8; }
            		    if (a >= 0xf0000000) { n1 +=  4; a >>= 4; }
            		    if (a >= 0x70000000) { n1 +=  2; a >>= 2; }
            		    if (a >= 0x30000000) n1 ++;
            		    return n1;
            		case 0x00:
            			//OVERFLOW?? does not overflow.
            		case 0x01:
            			return a + b;
            		case 0x04:
            			//OVERFLOW?? does not overflow.
            		case 0x05:
            			return b - a; //ACC value - multiplied result
            		case 0x02:
            			return (a * b) & 0xffffffffL;
            			
            	}
            case 0x1F:
            	switch(InstructionExtractor.funct(instruction)) {
            		case 0x18:
            			return (long) (short) (a & 0xffff);
            		case 0x10:
            			return (long) (char) (a & 0xff);
            		case 0x20:
            			long x = a & 0x00ff00ff;
            			long y = a & 0xff00ff00;
            			x = (int) (x << 8);
            			y = (int) (y >>> 8);
            			return x + y;
            		case 0x00:
            			int position = InstructionExtractor.sa(instruction);
            			int size = (InstructionExtractor.rd(instruction) + 1 - position);
            			long z = a << (32 - (position + size));
            			z = z & 0xffffffff;
            			z = z >>> (32 - position - 1);
            			return z;
            		case 0x04:
            			int position1 = InstructionExtractor.sa(instruction);
            			int size1 = (InstructionExtractor.rd(instruction) + 1 - position1);
            			long temp1 = b >>> (position1 + size1);
						long valToAdd = b - (temp1 << (position1 + size1));
						temp1 = temp1 << size1;
						//Clearing a
						a = a << (32 - size1);
						a &= 0xffffffff;
						a = a >>> (32 - size1);
						temp1 |= a;
						temp1 = temp1 << position1;
						temp1 += valToAdd;
						return temp1;
            	}
		}
		//@formatter:on
		return -1;
	}
	
}
