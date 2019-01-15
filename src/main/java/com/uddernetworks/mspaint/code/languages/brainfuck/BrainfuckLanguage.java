package com.uddernetworks.mspaint.code.languages.brainfuck;

import com.uddernetworks.mspaint.code.ImageClass;
import com.uddernetworks.mspaint.code.languages.DefaultJFlexLexer;
import com.uddernetworks.mspaint.code.languages.Language;
import com.uddernetworks.mspaint.code.languages.LanguageError;
import com.uddernetworks.mspaint.imagestreams.ImageOutputStream;
import com.uddernetworks.mspaint.main.MainGUI;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrainfuckLanguage implements Language {

    @Override
    public String getName() {
        return "Brainfuck";
    }

    @Override
    public String[] getFileExtensions() {
        return new String[]{"brainfuck", "bf", "b"};
    }

    @Override
    public String getOutputFileExtension() {
        return null;
    }

    @Override
    public boolean isInterpreted() {
        return true;
    }

    @Override
    public boolean meetsRequirements() {
        return true;
    }

    @Override
    public DefaultJFlexLexer getLanguageHighlighter() {
        return new BrainfuckLexer();
    }

    @Override
    public Map<ImageClass, List<LanguageError>> compileAndExecute(List<ImageClass> imageClasses, File outputFile, File otherFiles, File classOutputFolder, MainGUI mainGUI, ImageOutputStream imageOutputStream, ImageOutputStream compilerStream, List<File> libs, boolean execute) throws IOException {
        Map<ImageClass, List<LanguageError>> errors = new HashMap<>();

        PrintStream imageOut = new PrintStream(imageOutputStream);
        PrintStream compilerOut = new PrintStream(compilerStream);
        compilerStream.changeColor(Color.RED);

        for (ImageClass imageClass : imageClasses) {
            long start = System.currentTimeMillis();

            PrintStream oldPS = System.out;
            System.setOut(imageOut);

            String code = imageClass.getText();

            compilerOut.println("Checking code...");
            mainGUI.setStatusText("Checking code...");

            MultiLineBlock multiLineBlock = new MultiLineBlock(code);

            String error = check(multiLineBlock);

            if (error != null) {
                errors.put(imageClass, Collections.singletonList(new BrainfuckError(multiLineBlock.getLine() + 1, multiLineBlock.getCharacter() + 1, imageClass.getInputImage().getName(), error)));

                compilerOut.println("Error on " + imageClass.getInputImage().getName() + " [" + multiLineBlock.getLine() + ":" + multiLineBlock.getCharacter() + "] " + error);

                compilerOut.println("Completed in " + (System.currentTimeMillis() - start));

                System.setOut(oldPS);
                return errors;
            }

            compilerOut.println("Executing...");
            mainGUI.setStatusText("Executing...");

            start = System.currentTimeMillis();

            execute(code);

            System.setOut(oldPS);

            compilerOut.println("Executed in " + (System.currentTimeMillis() - start) + "ms");
            mainGUI.setStatusText("");
        }

        return errors;
    }

    @Override
    public String toString() {
        return getName();
    }

    private void execute(String code) {
        String[] parts = code.split("!");
        if (parts.length == 1) return;

        String input = parts[parts.length - 1].split("\n")[0].trim();
        int lastInput = 0;

        final int LENGTH = 65535;
        byte[] mem = new byte[LENGTH];
        int dataPointer = 0;

        int l = 0;
        for (int i = 0; i < code.length(); i++) {
            switch (code.charAt(i)) {
                case '>':
                    dataPointer = (dataPointer == LENGTH - 1) ? 0 : dataPointer + 1;
                    break;
                case '<':
                    dataPointer = (dataPointer == 0) ? LENGTH - 1 : dataPointer - 1;
                    break;
                case '+':
                    mem[dataPointer]++;
                    break;
                case '-':
                    mem[dataPointer]--;
                    break;
                case '.':
                    if (mem[dataPointer] != 0) System.out.print((char) mem[dataPointer]);
                    break;
                case ',':
                    mem[dataPointer] = (byte) (input.length() <= lastInput ? 0 : input.charAt(lastInput++));
                    break;
                case '[':
                    if (mem[dataPointer] == 0) {
                        i++;
                        while (l > 0 || code.charAt(i) != ']') {
                            if (code.charAt(i) == '[') l++;
                            if (code.charAt(i) == ']') l--;
                            i++;
                        }
                    }
                    break;
                case ']':
                    if (mem[dataPointer] != 0) {
                        i--;
                        while (l > 0 || code.charAt(i) != '[') {
                            if (code.charAt(i) == ']') l++;
                            if (code.charAt(i) == '[') l--;
                            i--;
                        }
                        i--;
                    }
                    break;
            }
        }
    }

    private String check(MultiLineBlock multiLineBlock) {
        int depth = 0;
        int extDepth = 0;
        int i = 0;
        while (multiLineBlock.canContinue()) {
            char current = multiLineBlock.getNext();
            switch (current) {
                case '[':
                    i++;
                    depth++;
                    extDepth++;
                    int tempI = i;
                    while (depth > 0 || multiLineBlock.getCharAt(i - 1) != ']') {
                        char curr = multiLineBlock.getCharAt(i);
                        if (curr == '[') depth++;
                        if (curr == ']') depth--;
                        i++;

                        if (i >= multiLineBlock.length()) return "Nothing to match [";
                    }
                    i = tempI - 1;
                    break;
                case ']':
                    if (extDepth == 0) {
                        return "Nothing to match ]";
                    } else {
                        extDepth--;
                    }
                    break;
            }

            i++;
        }

        return null;
    }
}
