package io.github.solclient.gradle;

import java.util.Objects;

public final class EntryTriple {

	public final String owner;
	public final String name;
	public final String desc;

	public EntryTriple(String owner, String name, String desc) {
		this.owner = owner;
		this.name = name;
		this.desc = desc;
	}

	@Override
	public int hashCode() {
		return Objects.hash(desc, name, owner);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof EntryTriple))
			return false;
		EntryTriple other = (EntryTriple) obj;
		return Objects.equals(desc, other.desc) && Objects.equals(name, other.name)
				&& Objects.equals(owner, other.owner);
	}

	@Override
	public String toString() {
		return "EntryTriple [owner=" + owner + ", name=" + name + ", desc=" + desc + "]";
	}

}
