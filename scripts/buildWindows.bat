@echo on

cd game

echo Setting up CI Workspace...
gradlew setupCIWorkspace
echo Building game...
gradlew build
cd ..

echo Packaging launcher...
electron-packager . sol-client-launcher --platform windows --arch x64 --out dist
node scripts\buildWindows.js
