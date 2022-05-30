# Sol Client


<a href="https://github.com/Sol-Client/Client/actions/workflows/build.yml"><img src="https://img.shields.io/github/workflow/status/Sol-Client/Client/build?style=for-the-badge&logo=github&logoColor=white"/></a>&nbsp;
<img src="https://img.shields.io/static/v1?label=you%20didn%27t&message=ask%20for%20this&color=blue&style=for-the-badge"/>&nbsp;
<img src="https://img.shields.io/static/v1?label=minecraft&message=1.8.9&color=brightgreen&style=for-the-badge"/>&nbsp;
<a href="https://discord.gg/QFDGDhcFqu"><img src="https://img.shields.io/discord/886561982872977408?color=5662F6&label=discord&logo=discord&logoColor=white&style=for-the-badge"/></a>&nbsp;
<img src="https://img.shields.io/static/v1?label=Contributions&message=Welcome&color=brightgreen&style=for-the-badge"/>&nbsp;
![GitHub all releases](https://img.shields.io/github/downloads/Sol-Client/Client/total?label=Downloads&style=for-the-badge)&nbsp;

Simple and easy to use Minecraft client for 1.8.9 (and in future™, newer versions).

Update: Yes, I know, it has been seven months since I wrote about the newer versions thing.

[Website](https://sol-client.github.io)

## Installing
First, go to [releases](https://github.com/Sol-Client/Client/releases).

Next, download the latest version for your OS. If there is a security warning, this is because we haven't spent any money on software signing (this client was created in my basement, okay), and many older and more trusted Minecraft projects (including FabricMC) also face this issue. The code is all open source, so don't worry.

## Licence
This project uses [GNU General Public License v3.0](LICENSE), meaning that if you fork this project, it must be either completely private, or the code *must* be available to your users.

This is not legal advice, or even a summary, this is just a simplification of one of the main parts of the licence.

While not stated in the licence, forks should have the following changed:
- Name
- Logo
- Design
This does not apply to third-party builds.

There are some parts of the code that use other licences (such as code "borrowed" from other projects). If this is the case, it will be noted in a comment, or another licence file in the same directory.

Note that this project is not 100% open source, because both Minecraft and OptiFine are closed source. There are 100% open source clients, but they do not inherit any of the game's code, meaning they are not as complete, and will likely get you banned on servers.

## Why?
There are many Minecraft clients, but they are usually closed source, and may contain malicious code. This client is free and open source, meaning that the code is visible, and anyone can propose changes and report issues with ease.

Many clients lack important features, including ReplayMod.

There may be mod loaders like Forge and Fabric, which are perfectly good, but are not as easy to use and update.

## Features

Here are the main features that are currently in the client:

- A clean HUD inspired by Lunar.
- Replay Mod. You will need to install FFmpeg yourself. This is completely unaffiliated to the original mod by Johni and CrushedPixel. If you encounter any issues, try reproducing them on the official Forge version, and if it happens there, report it on the [ReplayMod GitHub repositority](https://github.com/ReplayMod/ReplayMod). If it only happens on our version, report it here.
- Freelook (automatically disabled on Hypixel), press "V" to activate.
- Toggle sprint.
- Smooth zoom.
- Motion blur (if that's your thing).
- 1.7 animations.
- Item physics.
- Hypixel Additions.
- OptiFine, downloaded automatically from the official site (open an issue if this is a bad thing).
- Crosshair mod, allowing you to customise your crosshair, while fitting with the vanilla style.
- Quick Play Mod. Allows you to quickly join games at the press of a button (by default, "M"). The key opens a menu where you can search for games, navigating through them using the arrow keys. If you type nothing, you can see recent games and a categorised list of all games.
- Customisable launcher servers - automatically detected from the game, with no pinned servers.
- Better item tooltips - show item damage and more.
- Symbol picker (happy, sad, and most importantly: the snowman).
- Chat channel display.
- Pop-up events (pop-up friend request, etc.).
- Customisable font and colour scheme.
- Turning off the inventory logo.
- Speedometer.
- Chunk animator (not sure if many people use this one).
- Bedwars timers.
- Resource pack folders.

Click [this fancy blue text](https://github.com/Sol-Client/Client/projects/1) to see planned features.

If you want to suggest a feature, [create a new issue](https://github.com/Sol-Client/Client/issues/new) or [ask on Discord](https://discord.gg/QFDGDhcFqu).

If there are any issues with us using your code, please open an issue.

## Safety
The code for this client is available, meaning that you can see what it does.

This client has been tested by multiple people on Hypixel, and it disables disallowed mods automatically. There may be some unknown servers, but just disable the correct mods and you should be fine, as all Sol Client mods are designed to not change the behaviour of the player.

## Credits
[TheKodeToad](https://github.com/TheKodeToad) / [mcblueparrot](https://mine.ly/mcblueparrot.1): Programmer (turning tea into code).

[Holso](https://github.com/Holso) / [Dolfinc](https://mine.ly/Dolfinc.1): Helped create Discord, came up with the name (and changed his own one many times) and tested the client.

[sp614x](https://github.com/sp614x): OptiFine mod.

[Sk1er LLC](https://github.com/Sk1erLLC): Mod inspiration.

[Hyperium](https://github.com/HyperiumClient/Hyperium): Some rendering and launching code.

[Eric Golde](https://www.youtube.com/c/egold555): My older private client followed his tutorials (stole his code). This client was written from scratch (but not in Scratch).

[tr7zw](https://github.com/tr7zw/EntityCulling): Original EntityCulling mod. A modified version is used in this client.

[OrangeMarshall](https://namemc.com/profile/OrangeMarshall.1): Original 1.7 Animations mod.

[Johni0702](https://github.com/Johni0702) and [CrushedPixel](https://github.com/CrushedPixel): Replay Mod. Again, a modified version is used in this client.

[robere2](https://github.com/robere2): QuickPlay Mod servers. The mod itself is a totally different creature. Sorry for not providing you with analytics.

[lumien231](https://github.com/lumien231): Chunk Animator. The version in the client was based around the original Forge mod.

## IDE
### Eclipse
This IDE is prefered by the author, but many prefer IntelliJ for its IntelliJence (there's a clue in the name).

1. Right click in "Package Explorer" and press "Import Project".
2. Select "Gradle" > "Existing Gradle Project".
3. Select the "game" directory inside the repository folder.
4. Press "Finish".

### IntelliJ IDEA

1. Close your current project ("File" > "Close Project").
2. Press "Open".
3. Select the "game" directory inside the repository folder.

## Build Instructions

Want to contribute? Or are you just trying to re-enable freelook (please don't do this)? Want to port this to your toaster's operating system, or run this on Windows 98?

### Launcher

Make sure you have the latest Node.JS and NPM. You also need to build the game according to the instructions [below](#game).

Setup:
```sh
npm i
```

Start Launcher:
```sh
npm run start
```

Build Installer:
```sh
npm run make
```
The installer will be in a directory named "out".

### Game

Make sure to build it with Java 8 and not any newer versions.

Move into folder:
```sh
cd game
```

Setup:
```sh
./gradlew setupDecompWorkspace
```

Build JAR:
```sh
./gradlew build
```
You will find the result in build/libs/game.jar.

