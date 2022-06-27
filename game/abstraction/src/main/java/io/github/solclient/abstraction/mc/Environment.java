package io.github.solclient.abstraction.mc;

import lombok.experimental.UtilityClass;

/**
 * Various constants about the current environment and its capabilities.
 */
@UtilityClass
public class Environment {

	/**
	 * True if the sodium mod is installed in some form.
	 */
	public final boolean SODIUM = uval();
	/**
	 * True if the optifine mod is installed in some form.
	 */
	public final boolean OPTIFINE = uval();
	/**
	 * The optimisation engine: vanilla, sodium or optifine.
	 */
	public final OptimisationEngine OPTIMISATION_ENGINE = null;
	/**
	 * <code>true</code> if the current version has an offhand slot.
	 */
	public final boolean OFFHAND = uval();
	public final boolean BLOCKING = uval();
	public final boolean LWJGL3 = uval();
	/**
	 * <code>true</code> if plugin message use proper:parsable/identifiers that can
	 * be used as an Identifier object.
	 */
	public final boolean PROPER_PLUGIN_MESSAGE_IDS = uval();

	/**
	 * The target version - if this is a snapshot or prerelease, this will be the target version.
	 */
	public final String TARGET_VERSION = uval();
	/**
	 * The version id as x.x.x. Target Version is preferred since there may be
	 * snapshot testing versions of the client in future.
	 */
	public final String VERSION_ID = uval();
	/**
	 * Major release (according to semver) - Minecraft 1.7.10 = 1.
	 */
	public final int MAJOR_RELEASE = uval();
	/**
	 * Minor release (according to semver) - Minecraft 1.7.10 = 7.
	 */
	public final int MINOR_RELEASE = uval();
	/**
	 * Patch relase (according to semver) - Minecraft 1.7.10 = 10.
	 */
	public final int PATCH_RELEASE = uval();
	/**
	 * The protocol/networking version.
	 */
	public final int PROTOCOL_VERSION = uval();

	// Force the compiler not to optimise constants.
	// Otherwise, constants act as macros.
	private static <T> T uval() {
		return null;
	}

}