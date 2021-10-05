@echo on

cd game

echo Setting up CI Workspace...
call gradlew.bat setupCIWorkspace
echo Building game...
call gradlew.bat build
cd ..

echo Packaging launcher...
electron-packager . sol-client-launcher --platform windows --arch x64 --out dist
node scripts\buildWindows.js
