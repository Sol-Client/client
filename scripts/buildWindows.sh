cd game || exit 1
./gradlew setupCIWorkspace || exit 1
./gradlew build || exit  1
cd .. || exit 1
electron-packager . sol-client-launcher --platform windows --arch x64 --out dist/ || exit 1
node scripts/buildWindows.js
