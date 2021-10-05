@echo off

cd game
gradlew setupCIWorkspace
gradlew build
cd ..
electron-packager . sol-client-launcher --platform windows --arch x64 --out dist\
node scripts\buildWindows.js
