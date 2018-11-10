package com.uddernetworks.mspaint.project;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;

public enum BinaryIdentifier {
    INPUT_LOCATION(5, "inputLocation"),
    HIGHLIGHT_LOCATION(6, "highlightLocation"),
    OBJECT_LOCATION(7, "objectLocation"),
    CLASS_LOCATION(8, "classLocation"),
    JAR_FILE(9, "jarFile"),
    LIBRARY_LOCATION(10, "libraryLocation"),
    OTHER_LOCATION(11, "otherLocation"),
    COMPILER_OUTPUT(12, "compilerOutput"),
    APP_OUTPUT(13, "appOutput");

    public static final byte START_VALUE = 2;
    public static final byte END_VALUE = 3;

    private byte binary;
    private Field field;
    private String typeName;

    BinaryIdentifier(int binary, String field) {
        this.binary = (byte) binary;

        try {
            this.field = PPFProject.class.getDeclaredField(field);
            this.field.setAccessible(true);
            this.typeName = this.field.getType().getName().replaceAll("^.*\\.", "");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public byte getBinary() {
        return binary;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setValue(byte[] bytes, PPFProject ppfProject) {
        try {
            String string = new String(bytes);
            switch (typeName) {
                case "File":
                    field.set(ppfProject, new File(string));
                    break;
                case "String":
                    field.set(ppfProject, string);
                    break;
                case "boolean":
                    field.set(ppfProject, bytes[0] == 1);
                    break;
                case "int":
                    field.set(ppfProject, (int) bytes[0]);
                    break;
                case "double":
                    field.set(ppfProject, (double) bytes[0]);
                    break;
                default:
                    System.err.println("Not sure what to do with data value: " + string + " of class: " + typeName);
                    break;
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    public byte[] getValue(PPFProject ppfProject) {
        try {
            switch (typeName) {
                case "File":
                    return ((File) field.get(ppfProject)).getAbsolutePath().getBytes();
                case "String":
                    return ((String) field.get(ppfProject)).getBytes();
                case "boolean":
                    return new byte[] {field.getBoolean(ppfProject) ? (byte) 1 : 0};
                case "int":
                    return new byte[] {new Integer(field.getInt(ppfProject)).byteValue()};
                case "double":
                    return new byte[] {new Double(field.getDouble(ppfProject)).byteValue()};
                default:
                    System.err.println("Not sure what to do with field: " + field.getName() + " of class: " + typeName);
                    break;
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    public static BinaryIdentifier fromField(Field field) {
        return Arrays.stream(values()).filter(binaryIdentifier -> binaryIdentifier.field.equals(field)).findFirst().orElse(null);
    }

    public static BinaryIdentifier fromByte(byte binary) {
        return Arrays.stream(values()).filter(binaryIdentifier -> binaryIdentifier.binary == binary).findFirst().orElse(null);
    }
}
