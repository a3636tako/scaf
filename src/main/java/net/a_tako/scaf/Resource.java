package net.a_tako.scaf;

import java.nio.file.Path;

public interface Resource {
	void load(Path path);
	void initialize(Path path);
}
