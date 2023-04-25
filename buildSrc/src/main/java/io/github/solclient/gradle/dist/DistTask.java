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

		Path input = getInput().getAsFile().get().toPath();
		String version = getProject().getVersion().toString();

		PrismDist.export(this, input, folder.resolve("sol-client-prism-launcher-" + getProject().getVersion() + ".zip"),
				getProject(), version);

		MojankDist.export(this, input,
				folder.resolve("sol-client-mojang-launcher-" + getProject().getVersion() + ".zip"), getProject(),
				version);
	}

}
