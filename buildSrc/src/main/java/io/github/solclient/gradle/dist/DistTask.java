package io.github.solclient.gradle.dist;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ModuleDependency;
import org.gradle.api.artifacts.repositories.ArtifactRepository;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

public abstract class DistTask extends DefaultTask {

	@InputFile
	abstract RegularFileProperty getInput();

	@Input
	abstract Property<Configuration> getLibs();

	@OutputDirectory
	abstract RegularFileProperty getDestination();

	@TaskAction
	public void dist() throws IOException {
		Path folder = getDestination().getAsFile().get().toPath();
		Files.createDirectories(folder);
		PrismDist.export(this, getInput().getAsFile().get().toPath(),
				folder.resolve("sol-client-prism-launcher-" + getProject().getVersion() + ".zip"), getProject(),
				getLibs().get(), getProject().getVersion().toString());
	}

}
