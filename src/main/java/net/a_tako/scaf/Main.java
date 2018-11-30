package net.a_tako.scaf;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;

public class Main {
	public static void main(String[] args) throws Exception {
		new Main().run(args);
	}

	private Map<String, String> argumentsMap;
	private Parameters parameters;
	private Command command;

	public void run(String[] args) throws Exception {
		if(args.length < 1) {
			System.exit(1);
		}

		this.parameters = new Parameters(Arrays.asList(args)
			.subList(1, args.length));
		this.argumentsMap = parameters.toMap();

		try {
			command = (Command)Class.forName(args[0])
				.newInstance();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}

		Field[] fields = command.getClass()
			.getFields();

		Arrays.stream(fields)
			.filter(f -> f.isAnnotationPresent(Input.class))
			.forEach(this::setInputField);

		Arrays.stream(fields)
			.filter(f -> f.isAnnotationPresent(Output.class))
			.forEach(this::setOutputField);

		command.run(parameters);
	}

	private void setInputField(Field field) {
		try {
			String pathString = argumentsMap.get(field.getName());
			if(pathString == null || pathString.isEmpty()) {
				return;
			}
			Path path = Paths.get(pathString);

			Resource resource = (Resource)field.getType()
				.newInstance();

			resource.load(path);

			field.set(command, resource);
		} catch(InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private void setOutputField(Field field) {
		try {
			String pathString = argumentsMap.get(field.getName());
			if(pathString == null || pathString.isEmpty()) {
				pathString = ".";
			}

			Path path = Paths.get(pathString);

			Resource resource = (Resource)field.getType()
				.newInstance();

			resource.initialize(path);

			field.set(command, resource);
		} catch(InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
