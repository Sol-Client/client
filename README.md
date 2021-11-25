# Sol Client


<a href="https://github.com/TheKodeToad/Sol-Client/actions/workflows/build.yml"><img src="https://img.shields.io/github/workflow/status/TheKodeToad/Sol-Client/build?style=for-the-badge&logo=github&logoColor=white"/></a>&nbsp;
<img src="https://img.shields.io/static/v1?label=you%20didn%27t&message=ask%20for%20this&color=blue&style=for-the-badge"/>&nbsp;
<img src="https://img.shields.io/static/v1?label=minecraft&message=1.8.9&color=brightgreen&style=for-the-badge"/>&nbsp;
<a href="https://discord.gg/QFDGDhcFqu"><img src="https://img.shields.io/discord/886561982872977408?color=5662F6&label=discord&logo=discord&logoColor=white&style=for-the-badge"/></a>&nbsp;
![GitHub all releases](https://img.shields.io/github/downloads/TheKodeToad/Sol-Client/total?label=Downloads&style=for-the-badge)&nbsp;

Simple and easy to use Minecraft client for 1.8.9 (and in future, newer versions).

## Gimme it now!
First, go to [releases](https://github.com/TheKodeToad/Sol-Client/releases).

Next, download the latest version for your OS. If there is a security warning, this is because we haven't spent any money on software signing (this client was created in my basement, okay).

## Why?
There are many Minecraft clients, but they are usually closed source, and may contain malicious code.

There may be mod loaders like Forge and Fabric, which are perfectly good, but are not as simple and easy to use.

## Safety
I made the code for this client available, because not enough clients are open source (even OptiFine is closed source).

If you are suspicous, you can look at the code.

This client has been tested by multiple people on Hypixel, and it disables disallowed mods automatically.

## Credits
[TheKodeToad](https://github.com/TheKodeToad) / [mcblueparrot](https://mine.ly/mcblueparrot.1): Programmer.

[Holso](https://github.com/Holso) / [Chonnos](https://mine.ly/Chonnos.1): Helped create Discord, came up with the name (and changed his own one many times) and tested the client.

[sp614x](https://github.com/sp614x): OptiFine mod.

[Sk1er LLC](https://github.com/Sk1erLLC): Mod inspiration.

[Hyperium](https://github.com/HyperiumClient/Hyperium): Some rendering and launching code.

[Eric Golde](https://www.youtube.com/c/egold555): My older private client followed his tutorials.

[tr7zw](https://github.com/tr7zw/EntityCulling): Original EntityCulling mod. A modified version is used in this client.

[OrangeMarshall](https://namemc.com/profile/OrangeMarshall.1): Original 1.7 Animations.

[Johni0702](https://github.com/Johni0702) and [CrushedPixel](https://github.com/CrushedPixel): Replay Mod. Again, a modified version is used in this client.

[robere2] (https://github.com/robere2) QuickPlay Mod servers. The mod itself is coded from scratch.

## Features

Here are the main features that are currently in the client:

- A clean HUD inspired by Lunar.
- Freelook (automatically disabled on Hypixel), press "V" to activate.
- Toggle sprint.
- Smooth zoom.
- Motion blur.
- 1.7 animations.
- Item physics.
- Hypixel Additions.
- OptiFine, downloaded automatically from the official site.

Selling points (features not in Lunar Client, that are in this one):
- Replay Mod. You will need to install FFmpeg yourself.
- Quick Play Mod. Allows you to quickly join games at the press of a button (by default, "M"). The key opens a menu where you can search for games, navigating through them using the arrow keys. If you type nothing, you can see recent games and a categorised list of all games.
- Customisable launcher servers - automatically detected from the game, with no pinned servers.
- Better item tooltips - show item damage and more.
- Symbol picker (happy ☺, sad ☹, and best of all: ☃ the snowman).
- Chat channel display.
- Pop-up events (pop-up friend request, etc.).
- Customisable font and colour scheme.
- Turning off the inventory logo.
- Crosshair mod that doesn't look out of place (pixelated).
- Speedometer.
- Chunk animator (not sure if many people use this one).
- Less useless mods (apart from the previous one).
- Bedwars timers.

There are probably more, but this list is long enough.

Click [this fancy blue text](https://github.com/TheKodeToad/SolClient/projects/1) to see planned features.

If you want to suggest a feature, [create a new issue](https://github.com/TheKodeToad/Sol-Client/issues/new) or [ask on Discord](https://discord.gg/QFDGDhcFqu).

## License
This project uses the [MIT license](LICENSE). There are some parts of the code that use other licenses (such as code "borrowed" from other projects). If this is the case, it will be noted in a comment, or another license file in the same directory.

If there are any issues with us using your code, please open an issue.

## Build Instructions

### Launcher

Setup:
```sh
npm i
```

Start Launcher:
```sh
npm run start
```

Build Executable:
```sh
npm run make
```

### Game

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

