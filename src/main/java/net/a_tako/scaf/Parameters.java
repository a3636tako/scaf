package net.a_tako.scaf;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class Parameters {

	private List<String> args;
	
	public Parameters(List<String> args) {
		this.args = args;
	}
	
	public Map<String, String> toMap() {
		int state = 0;
		Map<String, String> result = new HashMap<>();
		String optionName = null;
		Iterator<String> iterator = args.iterator();
		String current = next(iterator);
		while(current != null) {
			switch(state) {
			case 0:
				if(current.startsWith("--")) {
					optionName = current.substring(2);
				}else if(current.startsWith("-")) {
					optionName = current.substring(1);
				}else {
					throw new RuntimeException("argument parsing error");
				}
				current = next(iterator);
				state = 1;
				
				break;

			case 1:
				if(current.startsWith("--") || current.startsWith("-")) {
					result.put(optionName, Boolean.TRUE.toString());
				}else {
					result.put(optionName, current);
					current = next(iterator);
				}
				optionName = null;
				state = 0;
				break;
			}
		}
		if(optionName != null) {
			result.put(optionName, Boolean.TRUE.toString());
		}
		return result;
	}
	
	private String next(Iterator<String> iterator) {
		return iterator.hasNext() ? iterator.next() : null;
	}
	
	public <T> T parseArgument(T bean) {
		CmdLineParser parser = new CmdLineParser(bean);
		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			parser.printUsage(System.err);
			System.exit(1);
		}
		
		return bean;
	}
}
