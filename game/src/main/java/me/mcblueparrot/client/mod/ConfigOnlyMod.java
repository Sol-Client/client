package me.mcblueparrot.client.mod;

/**
 * Represents a mod that cannot be disabled.
 */
public abstract class ConfigOnlyMod extends Mod {

	public ConfigOnlyMod(String name, String id, String description, ModCategory category) {
		super(name, id, description, category);
	}

	@Override
	public boolean isEnabled() {
		return isEnabledByDefault();
	}

	@Override
	public boolean isEnabledByDefault() {
		return true;
	}

	@Override
	public boolean isLocked() {
		return true;
	}

}
