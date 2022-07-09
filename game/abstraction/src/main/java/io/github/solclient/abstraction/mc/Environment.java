package io.github.solclient.abstraction.mc;

import lombok.experimental.UtilityClass;

/**
 * Various constants about the current environment and its capabilities.
 */
@UtilityClass
public class Environment {

	public static final ClassLoader CLASS_LOADER = null;
	/**
	 * True if the sodium mod is installed in some form.
	 */
	public final boolean SODIUM = RuntimeDetermined.value();
	/**
	 * True if the optifine mod is installed in some form.
	 */
	public final boolean OPTIFINE = RuntimeDetermined.value();
	/**
	 * The optimisation engine: vanilla, sodium or optifine.
	 */
	public final OptimisationEngine OPTIMISATION_ENGINE = null;
	/**
	 * <code>true</code> if the current version has an offhand slot.
	 */
	public final boolean OFFHAND = RuntimeDetermined.value();
	public final boolean BLOCKING = RuntimeDetermined.value();
	public final boolean LWJGL3 = RuntimeDetermined.value();

	/**
	 * <code>true</code> if plugin message use proper:parsable/identifiers that can
	 * be used as an Identifier object.
	 */
	public final boolean PROPER_PLUGIN_MESSAGE_IDS = RuntimeDetermined.value();

	/**
	 * The target version - if this is a snapshot or prerelease, this will be the target version.
	 */
	public final String TARGET_VERSION = RuntimeDetermined.value();
	/**
	 * The version id as x.x.x. Target Version is preferred since there may be
	 * snapshot testing versions of the client in future.
	 */
	public final String VERSION_ID = RuntimeDetermined.value();
	/**
	 * Major release (according to semver) - Minecraft 1.7.10 = 1.
	 */
	public final int MAJOR_RELEASE = RuntimeDetermined.value();
	/**
	 * Minor release (according to semver) - Minecraft 1.7.10 = 7.
	 */
	public final int MINOR_RELEASE = RuntimeDetermined.value();
	/**
	 * Patch relase (according to semver) - Minecraft 1.7.10 = 10.
	 */
	public final int PATCH_RELEASE = RuntimeDetermined.value();
	/**
	 * The protocol/networking version.
	 */
	public final int PROTOCOL_VERSION = RuntimeDetermined.value();

}