package edu.asu.plp.tool.prototype.util;

import static java.nio.ByteOrder.BIG_ENDIAN;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class LongUtils {
	
	public static long smartParse(String string)
	{
		Map<String, Function<String, Long>> prefixes = new HashMap<>();
		prefixes.put("0x", (s) -> Long.parseLong(s.substring(2), 16));
		prefixes.put("0b", (s) -> Long.parseLong(s.substring(2), 2));
		prefixes.put("0d", (s) -> Long.parseLong(s.substring(2), 10));
		prefixes.put("0o", (s) -> Long.parseLong(s.substring(2), 8));
		
		String prefix = (string.length() > 2) ? string.substring(0, 2) : null;
		Function<String, Long> conversion = prefixes.get(prefix);
		if (conversion == null)
			conversion = (s) -> Long.parseLong(s);
		
		return conversion.apply(string);
	}
	
	public static char[] toAsciiArray(long longvalue)
	{
		String asciiString = toAsciiString(longvalue);
		return asciiString.toCharArray();
	}
	
	public static String toAsciiString(long longvalue)
	{
		byte[] bytes = toByteArray(longvalue);
		return new String(bytes);
	}
	
	public static byte[] toByteArray(long longvalue)
	{
		ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES).order(BIG_ENDIAN);
		buffer.putLong(longvalue);
		return buffer.array();
	}

}
