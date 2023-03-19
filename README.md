<div align="center">

<img src="https://raw.githubusercontent.com/Sol-Client/client/develop/src/main/resources/assets/sol_client/textures/gui/icon.png" alt="Sol Client's logo">

# Sol Client

An **open source**, non-hacked client for Minecraft 1.8.9 (newer versions coming in the future)

<a href="https://www.java.com"><img alt="Java" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/built-with/java_vector.svg"></a>
<a href="https://discord.gg/TSAkhgXNbK"><img alt="Discord Server" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/social/discord-plural_vector.svg"></a>
<a href="https://sol-client.github.io"><img alt="Website" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/documentation/website_vector.svg"></a>

</div>

## 🤔 Why Sol over others?

**→ Easy to use** <br />
Everything you want to have is where want it to be. No confusing options, insatllation etc. It _just works_

**→ Consistent** <br />
One menu, one UI. Everything's listed the same way.

**→ Open source and non-profit** <br />
Anyone can modify, redistribute or see the source code of Sol. If you have privacy concerns or curiosity, you can also see for yourself that you are _not_ being spied on.

**→ Active development** <br />
Suggested features are always being considered and usually added.

## 📖 Features

- Customisable HUD
- Freelook
- Zoom
- Motion blur
- Menu blur
- 1.7 animations
- Item physics
- Hypixel additions
- OptiFine (optional, from [its website](https://optifine.net/downloads))
- Custom crosshairs
- Quick Play (inspired from robere2's [QuickPlay](https://github.com/QuickplayMod/quickplay))
- Better item tooltips (like the ones in Bedrock Edition)
- Chat symbol picker
- Chat channel picker and status
- Popup events (based on Sk1er's [PopupEvents](https://github.com/Sk1erLLC/PopupEvents))
- Speedometer
- Animated chunks (based on Lumien's [Chunk Animator](https://github.com/lumien231/Chunk-Animator))
- Timers
- Resource pack folders
- Replay Mod (using the actual [Replay Mod](https://github.com/ReplayMod/ReplayMod))
- Entity Culling (using an outdated version of tr7zw's [Entity Culling](https://github.com/tr7zw/EntityCulling) mod!!, since the owner adopted a more restrictive license)

**..and even more!**

## Alternatives

- [<img src="https://axolotlclient.github.io/images/icon.png" alt="AC icon" width="18"> AxolotlClient](https://axolotlclient.github.io/) - useful if you want to play on newer versions of Minecraft

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

Before a new release is created, it must be tested if Sol:

- Compiles
- Runs in development
- Runs the first or second time the client is game is launched on any machine
- Works in normal gameplay, with the new features enabled. This may mean releases take longer, but it is probably worth it
- The old features still work correctly
- Plays nicely with Watchdog (and other anticheats)
