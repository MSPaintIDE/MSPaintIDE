package com.uddernetworks.mspaint.main;

import com.uwyn.jhighlight.JHighlight;
import com.uwyn.jhighlight.renderer.XhtmlRendererFactory;
import com.uwyn.jhighlight.tools.FileUtils;
import net.sourceforge.tess4j.*;
import net.sourceforge.tess4j.util.LoadLibs;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;

import static net.sourceforge.tess4j.ITessAPI.TessOcrEngineMode.OEM_TESSERACT_CUBE_COMBINED;

public class Main {

    /*

    Best results:
    - Verdana

     */

    public static void main(String[] args) throws IOException {
        File file = new File("E:\\MSPaintIDE\\Main.png");
        File output = new File("E:\\MSPaintIDE\\Highlighted.html");

        output.createNewFile();

        //In case you don't have your own tessdata, let it also be extracted for you
        File tessDataFolder = LoadLibs.extractTessResources("tessdata");

        String text = getText(file, tessDataFolder);

        System.out.println("text = \n" + text);

//        JHighlight jHighlight = new JHighlight();



        XhtmlRendererFactory.getRenderer("java").highlight("Main.java", new ByteArrayInputStream(text.getBytes()), new FileOutputStream(output), null, false);
//        XhtmlRendererFactory.getRenderer("java").highlight("Main.java", in.toURL().openStream(), new FileOutputStream(out), null, false);
    }

    private static String getText(File file, File tessDataFolder) {
        ITesseract instance = new Tesseract();
//        instance.setOcrEngineMode(OEM_TESSERACT_CUBE_COMBINED);

//        instance.setTessVariable("preserve_interword_spaces", "0");
//        instance.setConfigs(Collections.singletonList("config.txt"));

//        Method getApiMethod = Tesseract.class.getDeclaredMethod("getAPI");
//        getApiMethod.setAccessible(true);
//
//        TessAPI api = (TessAPI) getApiMethod.invoke(instance);
//
//        api.

        instance.setDatapath(tessDataFolder.getAbsolutePath());

        try {
            return instance.doOCR(file);
        } catch (TesseractException e) {
            e.getMessage();
            return "Error while reading image!";
        }
    }

}
