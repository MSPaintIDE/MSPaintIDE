@echo off
setlocal enabledelayedexpansion
cls
title MS Paint IDE Codebase Setup
type setup\\ascii-colored.txt
echo.
echo.
echo.
echo This is the IDE's setup script to make developing with the IDE's codebase easier.
PAUSE
echo.
echo [91mJava 12 JDK[0m
echo Please type the root path of your Java 12 JDK, used for compiling and running the IDE:
set /p jdk12="[93m"
echo [0m
:jdk13_prompt
echo [91mJava 13 JDK[0m
echo The Java 13 JDK ([1mWITH[0m jpackage) is required for packaging with jpackage.
echo To open a link to download the JDK and reprompt this message, type [92m"link"[0m.
echo If you don't have the JDK, you may also type [92m"skip"[0m and it can be added later.
echo Please type [92m"link"[0m, [92m"skip"[0m, or the root path of your Java 13 JDK:
set /p jdk13="[93m"
echo [0m

if "%jdk13%" == "link" (
    echo [90mOpening URL...[0m
    echo.
    start "" https://download.java.net/java/early_access/jpackage/49/openjdk-13-jpackage+49_windows-x64_bin.zip
    goto :jdk13_prompt
)

if "%jdk13%" == "skip" (
    set jdk13 = "# Skipped #"
    echo [90mSkipping JDK 13...[0m
    echo.
)

if not defined jdk12 (
    echo [31mNo value for Java 12, exiting without any file changes...
    exit /b
)

if not defined jdk13 (
    echo [31mSuspected termination, exiting without any file changes...
    exit /b
)

REM This makes it so you don't commit your changes to gradle.properties
git update-index --assume-unchanged gradle.properties

set word=\\
set jdk12=%jdk12:\=!word!%
set jdk13=%jdk13:\=!word!%

echo [90mReplacing data in gradle.properties...[0m
powershell -Command "(gc gradle.properties) -replace 'jdk12', '!jdk12!' | Out-File -encoding ASCII gradle.properties"
powershell -Command "(gc gradle.properties) -replace 'jdk13', '!jdk13!' | Out-File -encoding ASCII gradle.properties"

echo.
echo Complete^^! This script may do more in the future, I mainly wanted to show off the sick colored ASCII art at the top.[0m
echo Press any key to exit...
PAUSE >nul
