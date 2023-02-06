# Sol Client
A nice Minecraft (non-hacked) client for 1.8.9 (and possibly newer versions in future)! This readme is probably still bad because I'm bad at formal.

## Features
- Customisable HUD
- Freelook
- Zoom
- Motion blur
- Menu blur
- 1.7 animations
- Item physics
- Hypixel additions
- OptiFine (optional, from optifine.net)
- Crosshair
- Quick play (command palette style) (uses games from robere2's mod)
- Better item tooltips, showing the full description
- Chat symbol picker
- Chat channel picker and status
- Popup events (based on [this](https://github.com/Sk1erLLC/PopupEvents))
- Speedometer
- Chunk animator (based on [this](https://github.com/lumien231/Chunk-Animator))
- Timers
- Pack folders
- Replay mod (using [this](https://github.com/ReplayMod/ReplayMod))
- Entity culling (using an outdated version of [this](https://github.com/tr7zw/EntityCulling)!! the owner adopted a more restrictive license since which is actually fairly understandable)

and even more!!

## Screenshots

### Launcher
![Launcher](./assets/screenshots/Launcher.png)

### Mods
This is the stable ui. There is another ui in the development version.

![Mods](./assets/screenshots/Mods.png)

## Building from source
Currently the launcher is in the same repo.

### Wrapper
The actual mod. You'll need Java 17 or later.

Run
```
./gradlew runClient
```

Build
```
./gradlew build
```

### Launcher
Configure
```
npm i
```

Run
```
npm run start
```

Build
```
npm run make
```
