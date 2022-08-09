package io.github.solclient.client.platform.mc;

import lombok.experimental.UtilityClass;

/**
 * Various constants about the current environment and its capabilities.
 */
@UtilityClass
public class Environment {

	public static final ClassLoader CLASS_LOADER = get("CLASS_LOADER");
	/**
	 * True if the sodium mod is installed in some form.
	 */
	public final boolean SODIUM = get("SODIUM");
	/**
	 * True if the optifine mod is installed in some form.
	 */
	public final boolean OPTIFINE = get("OPTIFINE");
	/**
	 * The optimisation engine: vanilla, sodium or optifine.
	 */
	public final OptimisationEngine OPTIMISATION_ENGINE = get("OPTIMISATION_ENGINE");
	/**
	 * <code>true</code> if the current version has an offhand slot.
	 */
	public final boolean OFFHAND = get("OFFHAND");
	public final boolean SWORD_BLOCKING = get("BLOCKING");
	public final boolean LWJGL3 = get("LWJGL3");

	/**
	 * <code>true</code> if plugin message use proper:parsable/identifiers that can
	 * be used as an Identifier object.
	 */
	public final boolean PROPER_PLUGIN_MESSAGE_IDS = get("PROPER_PLUGIN_MESSAGE_IDS");

	/**
	 * The target version - if this is a snapshot or prerelease, this will be the target version.
	 */
	public final String TARGET_RELEASE = get("TARGET_VERSION");
	/**
	 * The version id as x.x.x. Target Version is preferred since there may be
	 * snapshot testing versions of the client in future.
	 */
	public final String VERSION_ID = get("VERSION_ID");
	/**
	 * Major release (according to semver) - Minecraft 1.7.10 = 1.
	 */
	public final int MAJOR_RELEASE = get("MAJOR_RELEASE");
	/**
	 * Minor release (according to semver) - Minecraft 1.7.10 = 7.
	 */
	public final int MINOR_RELEASE = get("MINOR_RELEASE");
	/**
	 * Patch release (according to semver) - Minecraft 1.7.10 = 10.
	 */
	public final int PATCH_RELEASE = get("PATCH_RELEASE");
	/**
	 * The protocol/networking version.
	 */
	public final int PROTOCOL_VERSION = get("PROTOCOL_VERSION");

	private static <T> T get(String key) {
		throw new UnsupportedOperationException();
	}

}