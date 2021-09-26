# SolClient
Simple and lightweight Minecraft client for 1.8.9 (and in future, newer versions).

In October 2020, I decided to start working on my own Minecraft client, and nearly a year later, I've finally got somewhere.

## Why?
I know there are too many new clients, but they are usually closed-source clients, that may contain malicious code.

## Safety
I open sourced this client because not enough clients are open-source (even OptiFine is closed-source).

If you are suspicous, you can easily look through all the code, and if you want to use part of the code in your mod, it is fine as long as you follow [the license](LICENSE).

This client has been tested on Hypixel, and it even disables the mods automatically.

## Credits
[TheKodeToad](https://github.com/TheKodeToad) / mcblueparrot: Programmer.

[Holso](https://github.com/Holso) / IceDracon: Helped create Discord and came up with the name (and some other stuff).

## Features
- Most features from Lunar.
- Hypixel Additions (pop-up events, better channel switcher (shows channel name in chat box).
- Better item tooltips (shows item damage).
- Symbol picker (and maybe Emoji support?).
- OptiFine, downloaded automatically from the official site.

Click [this fancy blue text](https://github.com/TheKodeToad/SolClient/projects/1) to see planned features.

## Build Instructions

### Setup
```sh
npm i
```

### Start Launcher
```sh
npm run start
```

### Build Executable
```sh
npm run make
```
