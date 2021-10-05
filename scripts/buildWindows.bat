@echo off

cd game

call gradlew.bat setupCIWorkspace
call gradlew.bat build
cd ..

electron-packager . sol-client-launcher --out dist
node scripts\buildWindows.js
