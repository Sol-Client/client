package io.github.solclient.gradle.remapping;

import java.util.HashMap;
import java.util.Map;

import net.md_5.specialsource.JarMapping;

public final class ReverseMCP {

	private final Map<String, String> classes = new HashMap<>();
	private final Map<String, EntryTriple> methods = new HashMap<>();
	private final Map<String, EntryTriple> fields = new HashMap<>();

	public ReverseMCP(JarMapping mapping) {
		mapping.classes.forEach((src, dest) -> classes.put(dest, src));

		mapping.methods.forEach((src, dest) -> {
			// only use the first instance, we can handle this latter with class_parents if
			// yarn doesn't contain it
			if (methods.containsKey(dest))
				return;

			int nameSplit = src.lastIndexOf('/', src.indexOf(' '));
			methods.put(dest, new EntryTriple(src.substring(0, nameSplit),
					src.substring(nameSplit + 1, src.indexOf(' ')), src.substring(src.indexOf(' ') + 1)));
		});

		mapping.fields.forEach((src, dest) -> {
			if (fields.containsKey(dest))
				return;

			int nameSplit = src.lastIndexOf('/');
			fields.put(dest, new EntryTriple(src.substring(0, nameSplit), src.substring(nameSplit + 1), null));
		});
	}

	public String getClass(String clazz) {
		return classes.get(clazz);
	}

	public EntryTriple getMethod(String method) {
		return methods.get(method);
	}

	public EntryTriple getField(String field) {
		return fields.get(field);
	}

}
