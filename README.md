# Sol Client


<a href="https://github.com/TheKodeToad/Sol-Client/actions/workflows/build.yml"><img src="https://img.shields.io/github/workflow/status/TheKodeToad/Sol-Client/build?style=for-the-badge&logo=github&logoColor=white"/></a>&nbsp;
<img src="https://img.shields.io/static/v1?label=you%20didn%27t&message=ask%20for%20this&color=blue&style=for-the-badge"/>&nbsp;
<img src="https://img.shields.io/static/v1?label=minecraft&message=1.8.9&color=brightgreen&style=for-the-badge"/>&nbsp;
<a href="https://discord.gg/QFDGDhcFqu"><img src="https://img.shields.io/discord/886561982872977408?color=5662F6&label=discord&logo=discord&logoColor=white&style=for-the-badge"/></a>&nbsp;
![GitHub all releases](https://img.shields.io/github/downloads/TheKodeToad/Sol-Client/total?label=Downloads&style=for-the-badge)&nbsp;

Simple and easy to use Minecraft client for 1.8.9 (and in future™, newer versions).

[Screenshots](assets/screenshots/README.md)

## Gimme it now!
First, go to [releases](https://github.com/TheKodeToad/Sol-Client/releases).

Next, download the latest version for your OS. If there is a security warning, this is because we haven't spent any money on software signing (this client was created in my basement, okay), and many older and more trusted Minecraft projects (including FabricMC) also face this issue. The code is all open source, so don't worry (and if you do, just look through the code (I say, like it's easy)).

## Why?
There are (too) many Minecraft clients, but they are usually closed source, and may contain malicious code. They often are even more locked down than vanilla Minecraft - Lunar Client banned all modifications, despite it being a modification to Minecraft itself. This client is free and open source, meaning that the code is visible, and anyone can propose changes and report issues with ease.

Many clients lack important features, including ReplayMod, and have an annoying logo in the corner of the screen.

There may be mod loaders like Forge and Fabric, which are perfectly good, but are not as simple and easy to use, or maintain.

## Safety
I made the code for this client available, because many clients go to extra lengths to make sure that the code is extremely hidden and obfuscated (including safe ones such as Lunar and Badlion, with the exception of OptiFine). While the biggest clients are much more trusted and tested by the community, they all had to start somewhere. The actual code of the client (that is still around) was first written in January 2021.

If you are suspicous, you can sift around the (admittedly large) repository.

This client has been tested by multiple people on Hypixel, and it disables disallowed mods automatically. There may be some unknown servers, but just disable the correct mods and you should be fine, as all Sol Client mods are designed to not change the behaviour of the player.

Sol Client is arguably more Hypixel-safe than Lunar Client - Lunar already has a reputation for making changes they probably shouldn't. Even in current versions, it has a fly boost mod that isn't disabled, giving players an advantage in build battle (not that I play that game too often), allows you to place blocks while breaking other blocks (1.7 animations, not sure why this is unfair, but it's not possible in vanilla), and the crosshair highlights invisible players. In older versions, there were apparently some other issues relating to the particles mod or something, and the CPS limit (probably because it was designed for their own network). <sup>\[<i><a href="/">citation needed</a></i>\]</sup>

## Sorry Lunar
I still think Lunar is a good client, in the same way that Chrome is the most popular browser, but Firefox continues to exist. It just depends what you want from a web browser. I like both browsers, but I personally prefer using Firefox. I also don't want there to be a "Minecraft client monopoly", where there are only two popular Minecraft clients, that are both closed source.

## Credits
[TheKodeToad](https://github.com/TheKodeToad) / [mcblueparrot](https://mine.ly/mcblueparrot.1): Programmer (turning tea into code).

[Holso](https://github.com/Holso) / [Draconish](https://mine.ly/Draconish.1): Helped create Discord, came up with the name (and changed his own one many times) and tested the client.

[sp614x](https://github.com/sp614x): OptiFine mod.

[Sk1er LLC](https://github.com/Sk1erLLC): Mod inspiration.

[Hyperium](https://github.com/HyperiumClient/Hyperium): Some rendering and launching code.

[Eric Golde](https://www.youtube.com/c/egold555): My older private client followed his tutorials (stole his code). This client was written from scratch (but not in Scratch).

[tr7zw](https://github.com/tr7zw/EntityCulling): Original EntityCulling mod. A modified version is used in this client.

[OrangeMarshall](https://namemc.com/profile/OrangeMarshall.1): Original 1.7 Animations mod.

[Johni0702](https://github.com/Johni0702) and [CrushedPixel](https://github.com/CrushedPixel): Replay Mod. Again, a modified version is used in this client.

[robere2](https://github.com/robere2): QuickPlay Mod servers. The mod itself is a totally different creature. Sorry for not providing you with analytics.

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
- OptiFine, downloaded automatically from the official site (open an issue if this is a bad thing).
- Crosshair mod, allowing you to customise your crosshair, while fitting with the vanilla style.  

Selling points:
- Replay Mod. You will need to install FFmpeg yourself. This is completely unaffiliated to the original mod by Johni and CrushedPixel. If you encounter any issues, try reproducing them on the official Forge version, and if it happens there, report it on the [ReplayMod GitHub repositority](https://github.com/ReplayMod/ReplayMod). If it only happens on our version, report it here.
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
- Less useless mods (apart from the previous one).
- Bedwars timers.

There are probably more, but this list is long enough.

Click [this fancy blue text](https://github.com/TheKodeToad/SolClient/projects/1) to see planned features.

If you want to suggest a feature, [create a new issue](https://github.com/TheKodeToad/Sol-Client/issues/new) or [ask on Discord](https://discord.gg/QFDGDhcFqu).

## License
This project uses the [MIT license](LICENSE). There are some parts of the code that use other licenses (such as code "borrowed" from other projects). If this is the case, it will be noted in a comment, or another license file in the same directory.

If there are any issues with us using your code, please open an issue.

## Build Instructions

Want to contribute? Or are you just trying to re-enable freelook (please don't do this)? Want to port this to your toaster's operating system, or run this on Windows 98?

### Launcher

Make sure you have the latest Node.JS and NPM.

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

### Game

Make sure you have Java JDK 8 installed.

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
(into build/libs/game.jar)

