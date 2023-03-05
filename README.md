<div align="center">

<img src="https://raw.githubusercontent.com/Sol-Client/client/develop/src/main/resources/assets/sol_client/textures/gui/icon.png" alt="Sol Client's logo">

# Sol Client

An **open source**, non-hacked client for Minecraft 1.8.9 (newer versions coming in the future)

<a href="https://www.java.com"><img alt="Java" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/built-with/java_vector.svg"></a>
<a href="https://discord.gg/TSAkhgXNbK"><img alt="Join Discord" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/social/discord-plural_vector.svg"></a>
<a href="https://sol-client.github.io"><img alt="Website" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/documentation/website_vector.svg"></a>

</div>

## 🤔 Why does this exist? I mean, Lunar seems to be good enough..

Sol Client is one of the _very very_ few Minecraft 1.8 clients that is open source and privacy friendly. This means that the code written for client to be made is available to the public, thus it's safe. So, anyone could make sure that Sol is not a virus or it doesn't spy on you. Lunar on the hand is the exact opposite. It is propriety software and **it spies on you** and they even say it on their website:

**From: https://www.lunarclient.com/privacy**
> We may also **create or collect device-identifiable information** (DII), such as cookies, statistical identifiers, **unique device and advertising identifiers**, **usernames**, and similar identifiers that are linkable to a browser or device. From these platforms, we may also receive other information, such as **your IP address**, user agent, timestamps, **precise and imprecise geolocation**, **sensor data**, **apps**, fonts, battery life information and **screen size**.

Not to mention the active developmenrt and that **every** suggested feature is considered and most likely added or planned to be added.

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
- Quick Play (uses games from robere2's mod)
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

<details><summary><h2>🧪 Contributing</h2></summary>

If you want to contribute features, use the [`development`](https://github.com/Sol-Client/client) branch. If you want to contribute bug fixes, use the [`stable`](https://github.com/Sol-Client/client/tree/stable) branch.

### Code Formatting

Please use standard Java formatting conventions (the default Eclipse formatting profile, but with indented switch cases).
Using statements instead of blocks is fine.
Use tabs for indentation, and asterisks if more than one class is imported from a package.

### Building

To compile the client, run: `./gradlew build`

To run it, execute the following command: `./gradlew runClient`.

### Testing

Before a new release is created, there must be tested if Sol:

- Compiles
- Runs in development
- Runs the first or second time the client is game is launched on any machine
- Works in normal gameplay, with the new features enabled. This may mean releases take longer, but it is probably worth it
- The old features still work correctly
- Plays nicely with Watchdog (and other anticheats)

</details>
