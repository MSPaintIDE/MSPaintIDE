set MODULE_PATH=%1
set INPUT=%2
set OUTPUT=%3
set JAR=%4
set VERSION=%5
set APP_ICON=%6

call "%JAVA_HOME%\bin\java.exe" ^
    -Xmx512M ^
    --module-path "%JAVA_HOME%\jmods" ^
    --add-opens jdk.jlink/jdk.tools.jlink.internal.packager=jdk.packager ^
    -m jdk.packager/jdk.packager.Main ^
    create-image ^
    --module-path "%MODULE_PATH%" ^
    --verbose ^
    --echo-mode ^
    --add-modules "javafx.controls,javafx.fxml" ^
    --input "%INPUT%" ^
    --output "%OUTPUT%" ^
    --name "MSPaintIDE" ^
    --main-jar "%JAR%" ^
    --version "%VERSION%" ^
    --jvm-args "--add-opens javafx.base/com.sun.javafx.reflect=ALL-UNNAMED" ^
    --icon "%APP_ICON%" ^
    --class "com.uddernetworks.mspaint.main.JFXWorkaround"