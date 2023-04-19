<div align="center">

<img src="https://raw.githubusercontent.com/Sol-Client/client/develop/src/main/resources/assets/sol_client/textures/gui/icon.png" alt="Sol Client's logo">

# Sol Client

A nice open source, non-hacked client modification for Minecraft 1.8.9 (newer versions coming in the future)

<a href="https://www.java.com"><img alt="Java" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/built-with/java_vector.svg"></a>
<a href="https://discord.gg/TSAkhgXNbK"><img alt="Discord Server" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/social/discord-plural_vector.svg"></a>
<a href="https://sol-client.github.io"><img alt="Website" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/documentation/website_vector.svg"></a>

</div>

## 🤔 Why?

### Easy to use
Designed to be easy for less advanced players! Mod loaders have advantages, but Sol Client is designed to work with good compatibility out of the box.

### Consistent
All modules use a consistent style unlike what is often found in modpacks. I would like to see this change - it's a hard problem - but I hope the community can figure something out!

### Free and open source
Anyone can view, modify and redistribute the source code. If you have privacy concerns or curiosity, you can ensure yourself that the code is not doing anything malicious.

### Active development
Suggested features are always being considered and often added.

## 📖 Features

- Customisable HUD
- Freelook
- Zoom
- Motion blur
- Menu blur
- 1.7 animations
- Item physics
- Hypixel addons
- OptiFine (optional, from [its website](https://optifine.net/downloads))
- Custom crosshairs (Lunar format support soon™)
- Quick Play (inspired from robere2's [QuickPlay](https://github.com/QuickplayMod/quickplay))
- Better item tooltips (like the ones in Bedrock Edition)
- Chat symbol picker
- Chat channel picker and status
- Popup events (based on Sk1er's [PopupEvents](https://github.com/Sk1erLLC/PopupEvents))
- Speedometer
- Animated chunks (based on Lumien's [Chunk Animator](https://github.com/lumien231/Chunk-Animator))
- Badlion-compatible timers
- Resource pack folders
- Replay Mod (modified from [ReplayMod](https://github.com/ReplayMod/ReplayMod) available on mod loaders :P)
- Entity Culling (using an outdated version of tr7zw's [Entity Culling](https://github.com/tr7zw/EntityCulling) mod!!, since the owner adopted a more restrictive license) (to be rewritten)

**..and even more!**

## Replay
Since ReplayMod is open source, we use some very questionable garbage to allow it to run outside of Forge (pending a rewrite)! The reason for many clients not being able to use it is because of the GPL license. This should be obvious, but only report issues upstream if they occur with their unmodified builds!

## Alternatives

- [AxolotlClient](https://axolotlclient.github.io/) - has a more vanilla style, and useful if you want to play on newer versions of Minecraft. It is used with a mod loader unlike Sol Client which makes it more flexible.
- Mod loaders - the most popular are [Quilt](https://github.com/QuiltMC), [Fabric](https://github.com/FabricMC) and [Forge](https://github.com/MinecraftForge).
- Worth mentioning: [OneConfig](https://github.com/Polyfrost/OneConfig). It doesn't entirely fix the inconsistency of mod loaders, but it's a good start and still in development.

## 💼 License

Sol Client is Free and Open Source Software (FOSS), licensed under the [GNU General Public License](LICENSE),<br />version 3.0

---

## 🧪 Contributing

If you want to contribute features, use the [`development`](https://github.com/Sol-Client/client) branch. If you want to contribute bug fixes, use the [`stable`](https://github.com/Sol-Client/client/tree/stable) branch.

### Code Formatting

Please use standard Java formatting conventions (the default Eclipse formatting profile, but with indented switch cases).
Using statements instead of blocks is fine.
Use tabs for indentation, and asterisks if more than one class is imported from a package.

### Building

To compile the client, run: `./gradlew build`

To run it, execute the following command: `./gradlew runClient`.

## 📝 Testing

Before a new release is created, these things should ideally be tested:

- Compiles
- Runs in development
- Runs the first or second time the client is game is launched on any machine
- Works in normal gameplay, with the new features enabled.
- The old features still work correctly
- Plays nicely with Watchdog (and other anticheats)
