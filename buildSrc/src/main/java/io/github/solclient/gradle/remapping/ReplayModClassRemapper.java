package io.github.solclient.gradle.remapping;

import java.util.List;
import java.util.Map;

import org.objectweb.asm.commons.Remapper;

import net.fabricmc.mappingio.tree.MappingTree;
import net.fabricmc.mappingio.tree.MappingTree.ClassMapping;
import net.fabricmc.mappingio.tree.MappingTree.MemberMapping;
import net.fabricmc.mappingio.tree.MappingTree.MethodMapping;

public final class ReplayModClassRemapper extends Remapper {

	private final ReverseMCP mcp;
	private final MappingTree yarn;
	private Map<String, List<String>> classParents;
	private final int namespace;

	public ReplayModClassRemapper(ReverseMCP mcp, MappingTree yarn, Map<String, List<String>> classParents,
			String namespace) {
		this.mcp = mcp;
		this.yarn = yarn;
		this.classParents = classParents;
		this.namespace = yarn.getNamespaceId(namespace);
	}

	@Override
	public String map(String internalName) {
		if (ReplayModRemapper.RELOCATIONS.containsKey(internalName))
			return ReplayModRemapper.RELOCATIONS.get(internalName);

		if (internalName.startsWith("net/minecraft/")) {
			String official = mcp.getClass(internalName);

			if (official != null)
				return yarn.getClass(official).getName(namespace);
		}

		return super.map(internalName);
	}

	private String map(String owner, String name, String desc, boolean field) {
		if (name.startsWith("func_") || name.startsWith("field_")
				|| (owner != null && owner.startsWith("net/minecraft/") && name.matches("[A-Z_]+"))) {
			EntryTriple entry = null;
			if (field)
				entry = mcp.getField(name);
			else
				entry = mcp.getMethod(name);

			if (entry == null)
				throw new IllegalStateException("Could not find entry by name " + name);

			MemberMapping mapping = null;
			mapping = get(yarn.getClass(entry.owner), entry.name, entry.desc, field);
			if (mapping == null)
				throw new IllegalStateException("Could not remap " + owner + '/' + name + ' ' + desc + " aka "
						+ entry.owner + '/' + entry.name + ' ' + entry.desc);

			return mapping.getName(namespace);
		}

		return null;
	}

	private MemberMapping get(ClassMapping owner, String name, String desc, boolean field) {
		if (field)
			return owner.getField(name, desc);

		MethodMapping mapping = owner.getMethod(name, desc);

		if (mapping == null && classParents.containsKey(owner.getSrcName())) {
			for (String parent : classParents.get(owner.getSrcName())) {
				MemberMapping alt = get(yarn.getClass(parent), name, desc, false);
				if (alt != null)
					return alt;
			}
		}

		return mapping;
	}

	@Override
	public String mapMethodName(String owner, String name, String desc) {
		String result = map(owner, name, desc, false);
		if (result != null)
			return result;

		return super.mapMethodName(owner, name, desc);
	}

	@Override
	public String mapFieldName(String owner, String name, String desc) {
		// HACK
		if (namespace == 0) {
			if (name.equals("GOLD"))
				return "field_5489";
			else if (name.equals("RESET"))
				return "field_5504";
		}

		String result = map(owner, name, desc, true);
		if (result != null)
			return result;

		return super.mapFieldName(owner, name, desc);
	}

}
