package edu.asu.plp.tool.prototype.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class IntegerUtils
{
	public static int smartParse(String string)
	{
		Map<String, Function<String, Integer>> prefixes = new HashMap<>();
		prefixes.put("0x", (s) -> Integer.parseInt(s.substring(2), 16));
		prefixes.put("0b", (s) -> Integer.parseInt(s.substring(2), 2));
		prefixes.put("0d", (s) -> Integer.parseInt(s.substring(2), 10));
		prefixes.put("0o", (s) -> Integer.parseInt(s.substring(2), 8));
		
		String prefix = (string.length() > 2) ? string.substring(0, 2) : null;
		Function<String, Integer> conversion = prefixes.get(prefix);
		if (conversion == null)
			conversion = (s) -> Integer.parseInt(s);
			
		return conversion.apply(string);
	}
}
