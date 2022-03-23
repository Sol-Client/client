# Sol Client



<img src="https://img.shields.io/static/v1?label=you%20didn%27t&message=ask%20for%20this&color=blue&style=for-the-badge"/>&nbsp;
<img src="https://img.shields.io/static/v1?label=minecraft&message=1.8.9&color=brightgreen&style=for-the-badge"/>&nbsp;
<img src="https://img.shields.io/static/v1?label=Contributions&message=Welcome&color=brightgreen&style=for-the-badge"/>&nbsp;

Simple and easy to use Minecraft client for 1.8.9 (and in future™, newer versions).

# Editing Instructions

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
The installer will be in a directory named "out".

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
You will find the result in build/libs/game.jar.

