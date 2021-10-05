@echo off

cd game
gradlew setupCIWorkspace || exit 1
gradlew build || exit  1
cd ..
electron-packager . sol-client-launcher --platform windows --arch x64 --out dist\ || exit 1
node scripts\buildWindows.js || exit 1
