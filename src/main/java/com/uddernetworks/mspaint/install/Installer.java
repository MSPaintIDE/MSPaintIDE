package com.uddernetworks.mspaint.install;

import com.uddernetworks.mspaint.main.Main;
import com.uddernetworks.mspaint.main.MainGUI;
import org.apache.tika.io.IOUtils;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

//import sun.management.VMManagement;

public class Installer {

    private List<String> commands = Arrays.asList(
            "reg add \"HKEY_CLASSES_ROOT\\*\\shell\\MSPaintIDE\" /d \"Edit with MS Paint IDE\"",
            "reg add \"HKEY_CLASSES_ROOT\\*\\shell\\MSPaintIDE\\command\" /t REG_EXPAND_SZ /d \"\\\"%LocalAppData%\\MSPaintIDE\\open.bat\\\" \\\"%1\\\"\"",
            "reg add \"HKEY_CLASSES_ROOT\\*\\shell\\MSPaintIDE\" /v Icon /d \"%LocalAppData%\\MSPaintIDE\\images\\ms-paint-logo.ico\"",
            "reg add \"HKEY_CLASSES_ROOT\\*\\shell\\MSPaintIDE\" /v Position /d \"Top\"",
            "reg add \"HKEY_CLASSES_ROOT\\.ppf\" /ve /d \"PaintProjectFile\"",
            "reg add \"HKEY_CLASSES_ROOT\\PaintProjectFile\\shell\\open\\command\" /ve /t REG_EXPAND_SZ /d \"%LocalAppData%\\MSPaintIDE\\open.bat \\\"%1\\\"\"",
            "reg add \"HKEY_CLASSES_ROOT\\PaintProjectFile\\DefaultIcon\" /ve /t REG_EXPAND_SZ /d \"%LocalAppData%\\MSPaintIDE\\images\\ms-paint-logo.ico\"",
            "ie4uinit.exe -show"
    );

    private List<String> removeRegistry = Arrays.asList(
            "reg delete \"HKEY_CLASSES_ROOT\\*\\shell\\MSPaintIDE\" /f",
            "reg delete \"HKEY_CLASSES_ROOT\\PaintProjectFile\" /f",
            "reg delete \"HKEY_CLASSES_ROOT\\.ppf\" /f"
    );

    public void install() {
        try {
            System.out.println("Started install");
            File msPaintAppData = MainGUI.LOCAL_MSPAINT;
            msPaintAppData.mkdirs();

            File imagesFolder = new File(msPaintAppData, "images");
            imagesFolder.mkdirs();

            new File(msPaintAppData, "database").mkdirs();

            File jdkPath = getJDKLocation();

            System.out.println("Using JDK path: " + jdkPath.getAbsolutePath());

            File currentJar = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            Path appDataJar = Paths.get(msPaintAppData.getAbsolutePath(), "MSPaintIDE.jar");
            Files.copy(currentJar.toPath(), appDataJar, StandardCopyOption.REPLACE_EXISTING);

            Path newIcon = Paths.get(imagesFolder.getAbsolutePath(), "ms-paint-logo.ico");
            Files.copy(getClass().getClassLoader().getResourceAsStream("ms-paint-logo.ico"), newIcon, StandardCopyOption.REPLACE_EXISTING);

            Path uninstallIcon = Paths.get(imagesFolder.getAbsolutePath(), "uninstall.ico");
            Files.copy(getClass().getClassLoader().getResourceAsStream("uninstall.ico"), uninstallIcon, StandardCopyOption.REPLACE_EXISTING);

            Path openBat = Paths.get(msPaintAppData.getAbsolutePath(), "open.bat");

            String open = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("open.bat"));
            open = open.replace("%APPDATA_JAR%", appDataJar.toString())
                .replace("%JAVAW_JDK%", jdkPath.getAbsolutePath());

            Files.write(openBat, open.getBytes(), StandardOpenOption.CREATE_NEW);

            for (String regCommand : commands) {
                regCommand = regCommand.replace("%APPDATA_BAT%", openBat.toAbsolutePath().toString());
                regCommand = regCommand.replace("%APPDATA_LOGO%", newIcon.toAbsolutePath().toString());
                String ran = runCommand(regCommand, false);
                if ("TIMEOUT".equals(ran)) {
                    break;
                }
            }

            Path shortcutGen = Paths.get(currentJar.getParentFile().getAbsolutePath(), "shortcut.vbs");

            String shortcut = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("shortcut.vbs"));
            shortcut = shortcut.replace("JDK_PATH_HERE", jdkPath.getAbsolutePath());

            Files.write(shortcutGen, shortcut.getBytes(), StandardOpenOption.CREATE_NEW);


            Path uninstallPath = Paths.get(msPaintAppData.getAbsolutePath(), "uninstall.vbs");
            Files.copy(getClass().getClassLoader().getResourceAsStream("uninstall.vbs"), uninstallPath);

            runCommand("cmd /c wscript \"" + shortcutGen.toAbsolutePath() + "\" && del \"" + shortcutGen.toAbsolutePath() + "\"", false);
            runCommand("cmd /c wscript \"" + uninstallPath.toAbsolutePath() + "\" && del \"" + uninstallPath.toAbsolutePath() + "\"", false);

            ShortcutUtil.makeAdmin(new File(msPaintAppData.getAbsolutePath() + "\\Uninstall MS Paint IDE.lnk"));

            Runtime.getRuntime().exec("cmd /c ping localhost -n 2 > nul && del \"" + currentJar.getAbsolutePath() + "\"");
        } catch (Exception e) {
            e.printStackTrace();

            System.out.println("An error has occurred during installation! Reverting anything changed during the process...");

            try {
                Path msPaintAppData = Paths.get(System.getProperties().getProperty("user.home"), "AppData\\Local\\MSPaintIDE").toAbsolutePath();

                File imagesFolder = new File(msPaintAppData.toString(), "images");
                if (imagesFolder.exists()) {
                    Arrays.stream(imagesFolder.listFiles()).forEach(File::delete);
                    imagesFolder.delete();
                }

                File currentJar = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());

                Files.deleteIfExists(Paths.get(msPaintAppData.toString(), "MSPaintIDE.jar"));
                Files.deleteIfExists(Paths.get(imagesFolder.getAbsolutePath(), "ms-paint-logo.ico"));
                Files.deleteIfExists(Paths.get(imagesFolder.getAbsolutePath(), "uninstall.ico"));
                Files.deleteIfExists(Paths.get(msPaintAppData.toString(), "open.bat"));
                Files.deleteIfExists(Paths.get(currentJar.getParentFile().getAbsolutePath(), "shortcut.vbs"));
                Files.deleteIfExists(Paths.get(msPaintAppData.toString(), "uninstall.vbs"));

                Files.deleteIfExists(msPaintAppData);

                for (String regCommand : removeRegistry) {
                    if ("TIMEOUT".equals(runCommand(regCommand, false))) break;
                }
            } catch (IOException | URISyntaxException e2) {
                e2.printStackTrace();

                System.out.println("Unable to successfully uninstall! Make sure everything is removed from the path: %LocalAppData%\\MSPaintIDE");
            }
        }
    }

    public void uninstall() throws ReflectiveOperationException {
        File msPaintAppData = MainGUI.LOCAL_MSPAINT;

        int currentPID = getProcessID();

        List<Integer> runningProcesses = Arrays.stream(runCommand("tasklist.exe /fo csv /nh /v /fi \"IMAGENAME eq javaw.exe\"", true)
                .split("\n"))
                .filter(line -> line.contains("MS Paint IDE"))
                .map(line -> Integer.valueOf(line.split(",")[1].replace("\"", "")))
                .filter(pid -> pid != currentPID)
                .collect(Collectors.toList());

        if (!runningProcesses.isEmpty()) {
            System.out.println("Found " + runningProcesses.size() + " process" + (runningProcesses.size() > 1 ? "es" : "") + " of MS Paint IDE running. Killing them...");

            runningProcesses.forEach(pid -> runCommand("taskkill /PID " + pid + " /F", false));

            System.out.println("Killed all processes. Continuing...");
        }

        for (String regCommand : removeRegistry) {
            if ("TIMEOUT".equals(runCommand(regCommand, false))) break;
        }

        runCommand("cmd /c ping localhost -n 3 > nul && rmdir \"" + msPaintAppData.getAbsolutePath() + "\" /q /s", false, false, new File("C:\\Windows\\system32"));
    }

    private List<String> findJavaIn(boolean x86) {
        List<String> jdkLines = Arrays.stream(runCommand("where javaw /r \"C:\\Program Files" + (x86 ? " (x86)" : "") + "\\Java\"", true)
                .split("\n"))
                .filter(line -> line.contains("\\Program Files") && line.contains("jdk") && line.contains("bin") && !line.contains("jre"))
                .sorted()
                .collect(Collectors.toList());
        Collections.reverse(jdkLines);
        return jdkLines;
    }

    public File getJDKLocation() throws FileNotFoundException {
        List<String> jdkLines = findJavaIn(false);
        if (jdkLines.isEmpty()) jdkLines = findJavaIn(true);

        if (jdkLines.isEmpty()) {
            throw new FileNotFoundException("No installed JDK found! Please run the program with the JDK manually if you know where it is.");
        }

        return new File(jdkLines.get(0));
    }

    private int getProcessID() throws ReflectiveOperationException{
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        Field jvm = runtime.getClass().getDeclaredField("jvm");
        jvm.setAccessible(true);
//        VMManagement mgmt = (VMManagement) jvm.get(runtime);
        Object mgmt = jvm.get(runtime);

        System.out.println("VM id: " + mgmt.getClass());

        Method pidMethod = mgmt.getClass().getDeclaredMethod("getProcessId");
        pidMethod.setAccessible(true);

        return (Integer) pidMethod.invoke(mgmt);
    }

    public static String runCommand(String command, boolean output) {
        return runCommand(command, output, true, new File("C:\\Windows\\system32"));
    }

    public static String runCommand(String command, boolean output, boolean wait, File directory) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            Runtime runtime = Runtime.getRuntime();
            long start = System.currentTimeMillis();
            Process process;
            process = runtime.exec(command, null, directory);

            if (output) {
                try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = input.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                }
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
