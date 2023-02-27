package io.github.solclient.gradle;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import org.apache.commons.codec.digest.DigestUtils;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.DependencyArtifact;
import org.gradle.api.artifacts.ModuleDependency;
import org.gradle.api.artifacts.repositories.ArtifactRepository;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;

public final class Utils {

	public static boolean remoteResourceExists(URL url) {
		try {
			url.openConnection().getInputStream().close();
			return true;
		} catch (IOException error) {
			return false;
		}
	}

	public static void download(URL url, Path destination, String sha1) throws IOException {
		if (Files.exists(destination) && checkHash(destination).equalsIgnoreCase(sha1))
			return;

		try (OutputStream out = Files.newOutputStream(destination)) {
			url.openStream().transferTo(out);
		}
	}

	private static String checkHash(Path file) throws IOException {
		try (InputStream in = Files.newInputStream(file)) {
			return DigestUtils.sha1Hex(in);
		}
	}

	public static String format(Dependency dependency) {
		String result = dependency.toString();
		if (dependency instanceof ModuleDependency module) {
			Iterator<DependencyArtifact> iterator = module.getArtifacts().iterator();
			if (iterator.hasNext()) {
				DependencyArtifact artifact = iterator.next();
				if (!iterator.hasNext() && artifact.getClassifier() != null) {
					result += ':';
					result += artifact.getClassifier();
				}
			}
		}
		return result;
	}

	public static URL concat(URL base, String relative) throws MalformedURLException {
		String str = base.toString();
		if (str.charAt(str.length() - 1) != '/')
			str += '/';
		return new URL(str + relative);
	}

	public static String getRepo(Dependency dependency, Iterable<ArtifactRepository> pool)
			throws MalformedURLException {
		for (ArtifactRepository repoable : pool) {
			if (repoable instanceof MavenArtifactRepository repo) {
				URL urlable = concat(repo.getUrl().toURL(),
						dependency.getGroup().replace('.', '/') + '/' + dependency.getName() + '/'
								+ dependency.getVersion() + '/' + dependency.getName() + '-' + dependency.getVersion()
								+ ".pom");
				if (!Utils.remoteResourceExists(urlable))
					continue;
				return repo.getUrl().toString();
			}
		}

		// TODO don't do
		System.err.println(dependency + " was not found :(");
		return null;
	}

}
