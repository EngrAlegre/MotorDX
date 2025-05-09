@echo off
echo Building Motor Diagnostic APK...

rem Set paths to Android SDK
set ANDROID_HOME=C:\Users\alegr\AppData\Local\Android\Sdk
set BUILD_TOOLS=%ANDROID_HOME%\build-tools\33.0.0

echo Using Android SDK at: %ANDROID_HOME%
echo Using Build Tools at: %BUILD_TOOLS%

rem Clean output directory
echo Cleaning previous builds...
if exist app\build\outputs\apk\release rmdir /S /Q app\build\outputs\apk\release
mkdir app\build\outputs\apk\release

rem Compile Java sources
echo Compiling Java sources...
javac -classpath "%ANDROID_HOME%\platforms\android-34\android.jar" -d app\build\classes app\src\main\java\com\motor\diagnostic\*.java

rem Build APK
echo Building APK...
%BUILD_TOOLS%\aapt package -f -m -J app\src\main\java -M app\src\main\AndroidManifest.xml -S app\src\main\res -I "%ANDROID_HOME%\platforms\android-34\android.jar" -F app\build\outputs\apk\release\app-release-unsigned.apk app\build\classes

echo Build completed. APK is at: app\build\outputs\apk\release\app-release-unsigned.apk 