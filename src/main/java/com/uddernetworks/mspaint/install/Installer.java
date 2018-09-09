package com.uddernetworks.mspaint.install;

import com.uddernetworks.mspaint.main.Main;
import org.apache.tika.io.IOUtils;

import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Installer {

    private List<String> regCommands = Arrays.asList(
            "reg add \"HKEY_CLASSES_ROOT\\*\\shell\\MSPaintIDE\" /d \"Edit with MS Paint IDE\"",
            "reg add \"HKEY_CLASSES_ROOT\\*\\shell\\MSPaintIDE\\command\" /d \"%APPDATA_BAT% \\\"%1\\\"\"",
            "reg add \"HKEY_CLASSES_ROOT\\*\\shell\\MSPaintIDE\" /v Icon /d \"%APPDATA_LOGO%\"",
            "reg add \"HKEY_CLASSES_ROOT\\*\\shell\\MSPaintIDE\" /v Position /d \"Top\""
    );

    private String removeRegistry = "reg delete \"HKEY_CLASSES_ROOT\\*\\shell\\MSPaintIDE\" /f";

    public void install() {
        try {
            System.out.println("Started install");
            File msPaintAppData = new File(System.getProperties().getProperty("user.home"), "AppData\\Local\\MSPaintIDE");
            msPaintAppData.mkdirs();

            File imagesFolder = new File(msPaintAppData, "images");
            imagesFolder.mkdirs();

            File currentJar = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            Path appDataJar = Paths.get(msPaintAppData.getAbsolutePath(), "MSPaintIDE.jar");
            Files.copy(currentJar.toPath(), appDataJar, StandardCopyOption.REPLACE_EXISTING);

            Path newIcon = Paths.get(imagesFolder.getAbsolutePath(), "logo.ico");
            Files.copy(getClass().getClassLoader().getResourceAsStream("logo.ico"), newIcon, StandardCopyOption.REPLACE_EXISTING);

            Path uninstallIcon = Paths.get(imagesFolder.getAbsolutePath(), "uninstall.ico");
            Files.copy(getClass().getClassLoader().getResourceAsStream("uninstall.ico"), uninstallIcon, StandardCopyOption.REPLACE_EXISTING);

            Path openBat = Paths.get(msPaintAppData.getAbsolutePath(), "open.bat");

            String open = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("open.bat"));
            open = open.replace("%APPDATA_JAR%", appDataJar.toString());

            Files.write(openBat, open.getBytes(), StandardOpenOption.CREATE_NEW);

            for (String regCommand : regCommands) {
                regCommand = regCommand.replace("%APPDATA_BAT%", openBat.toAbsolutePath().toString());
                regCommand = regCommand.replace("%APPDATA_LOGO%", newIcon.toAbsolutePath().toString());
                if ("TIMEOUT".equals(runCommand(regCommand, false))) {
                    break;
                }
            }

            Path shortcutGen = Paths.get(currentJar.getParentFile().getAbsolutePath(), "shortcut.vbs");

            File jdkPath = getJDKLocation();

            System.out.println("Using JDK path: " + jdkPath.getAbsolutePath());

            String shortcut = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("shortcut.vbs"));
            shortcut = shortcut.replace("JDK_PATH_HERE", jdkPath.getAbsolutePath());

            Files.write(shortcutGen, shortcut.getBytes(), StandardOpenOption.CREATE_NEW);


            Path uninstallPath = Paths.get(msPaintAppData.getAbsolutePath(), "uninstall.vbs");
            Files.copy(getClass().getClassLoader().getResourceAsStream("uninstall.vbs"), uninstallPath);


            Runtime.getRuntime().exec("cmd /c wscript \"" + shortcutGen.toAbsolutePath().toString() + "\" && del \"" + shortcutGen.toAbsolutePath().toString() + "\"");
            Runtime.getRuntime().exec("cmd /c wscript \"" + uninstallPath.toAbsolutePath().toString() + "\" && del \"" + uninstallPath.toAbsolutePath().toString() + "\"");


            Path adminShortcutGen = Paths.get(msPaintAppData.getAbsolutePath(), "AdminShortcut.ps1");

            String adminShortcut = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("AdminShortcut.ps1"));
            adminShortcut = adminShortcut.replace("%SHORTCUT_PATH%", msPaintAppData.getAbsolutePath() + "\\Uninstall MS Paint IDE.lnk");

            Files.write(adminShortcutGen, adminShortcut.getBytes(), StandardOpenOption.CREATE_NEW);

            runCommand("cmd /c Powershell -Command \"Set-ExecutionPolicy RemoteSigned\"", false);
            runCommand("cmd /c Powershell -File \"" + adminShortcutGen.toAbsolutePath().toString() + "\"", false);

            Runtime.getRuntime().exec("cmd /c ping localhost -n 2 > nul && del \"" + currentJar.getAbsolutePath() + "\"");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uninstall() {
        File msPaintAppData = new File(System.getProperties().getProperty("user.home"), "AppData\\Local\\MSPaintIDE");

        runCommand(removeRegistry, false);
        runCommand("cmd /c ping localhost -n 3 > nul && rmdir \"" + msPaintAppData.getAbsolutePath() + "\" /q /s", false, false, new File("C:\\Windows\\system32"));
    }

    public File getJDKLocation() throws FileNotFoundException {
        List<String> jdkLines = Arrays.stream(runCommand("where javaw", true).split("\n")).filter(line -> line.contains("\\Program Files") && line.contains("Java\\jdk") && line.contains("bin")).sorted().collect(Collectors.toList());
        Collections.reverse(jdkLines);

        if (jdkLines.size() == 0) {
            throw new FileNotFoundException("No installed JDK found! Please run the program with the JDK manually if you know where it is.");
        }

        return new File(jdkLines.get(0));
    }

    private String runCommand(String command, boolean output) {
        return runCommand(command, output, true, new File("C:\\Windows\\system32"));
    }

    private String runCommand(String command, boolean output, boolean wait, File directory) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            Runtime runtime = Runtime.getRuntime();
            long start = System.currentTimeMillis();
            Process process;
            process = runtime.exec(command, null, directory);

            if (output) {
                String line;
                BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
                while ((line = input.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }

                input.close();
                return stringBuilder.toString();
            }

            while (wait && process.isAlive()) {
                if (System.currentTimeMillis() - start >= 3000) {
                    process.destroyForcibly();
                    return "TIMEOUT";
                }
            }

            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
