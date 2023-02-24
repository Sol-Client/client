package io.github.solclient.gradle.dist;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

public abstract class DistTask extends DefaultTask {

	@InputFile
	abstract RegularFileProperty getInput();

	@OutputDirectory
	abstract RegularFileProperty getDestination();

	@TaskAction
	public void dist() throws IOException {
		Path folder = getDestination().getAsFile().get().toPath();
		Files.createDirectories(folder);
		PrismDist.export(getInput().getAsFile().get().toPath(), folder.resolve("prism.zip"),
				getProject().getVersion().toString());
	}

}
