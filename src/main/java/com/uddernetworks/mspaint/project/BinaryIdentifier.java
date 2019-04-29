package com.uddernetworks.mspaint.project;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
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
    APP_OUTPUT(13, "appOutput"),
    NAME(14, "name"),
    LANGUAGE(15, "language"),
    SYNTAX_HIGHLIGHT(16, "syntaxHighlight"),
    COMPILE(17, "compile"),
    EXECUTE(18, "execute"),
    ARRAY(23, "array");

    public static final byte START_VALUE = 2;
    public static final byte END_VALUE = 3;
    public static final byte START_ARRAY = 21;
    public static final byte BREAK_ARRAY = 22;
    public static final byte END_ARRAY = 23;

    private byte binary;
    private Field field;
    private String typeName;
    private boolean array;

    BinaryIdentifier(int binary, String field) {
        this.binary = (byte) binary;

        try {
            this.field = PPFProject.class.getDeclaredField(field);
            this.field.setAccessible(true);
            this.array = this.field.getType().isArray();
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
            if (this.array) {
                var array = new ArrayList<>();
                if (bytes[0] == START_ARRAY) {
                    var buffer = new ArrayList<Byte>();
                    for (int i = 1; i < bytes.length; i++) {
                        var curr = bytes[i];
                        if (curr == BREAK_ARRAY) {
                            array.add(processSetBytes(ByteUtils.byteListToArray(buffer)));
                            buffer.clear();
                            continue;
                        }

                        buffer.add(curr);
                    }

                    if (buffer.isEmpty()) {
                        array.add(processSetBytes(ByteUtils.byteListToArray(buffer)));
                    }
                } else {
                    System.out.println("Labeled as array but contains no ARRAY_START byte!");
                }

                var baseClass = Class.forName(this.field.getType().getCanonicalName().replace("[", "").replace("]", ""));

                var arr = Array.newInstance(baseClass, array.size());
                for (int i = 0; i < array.size(); i++) {
                    Array.set(arr, i, array.get(i));
                }

                field.set(ppfProject, arr);
            } else {
                field.set(ppfProject, processSetBytes(bytes));
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    private Object processSetBytes(byte[] bytes) {
        String string = new String(bytes);
        switch (typeName.replace(";", "")) {
            case "File":
                return new File(string);
            case "String":
                return string;
            case "boolean":
                return bytes[0] == 1;
            case "int":
                return (int) bytes[0];
            case "double":
                return (double) bytes[0];
            default:
                System.err.println("Not sure what to do with data value: " + string + " of class: " + typeName);
                break;
        }

        return null;
    }

    public byte[] getValue(PPFProject ppfProject) {
        try {
            if (field.get(ppfProject) == null) return new byte[0];
            if (this.array) {
                Object[] list = (Object[]) field.get(ppfProject);
                var bytes = new ArrayList<Byte>();
                bytes.add(START_ARRAY);
                Arrays.stream(list).forEach(element -> {
                    bytes.addAll(ByteUtils.byteArrayToList(processGetBytes(element)));
                    bytes.add(BREAK_ARRAY);
                });
                bytes.remove(bytes.size() - 1);
                bytes.add(END_ARRAY);
                return ByteUtils.byteListToArray(bytes);
            } else {
                return processGetBytes(field.get(ppfProject));
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    private byte[] processGetBytes(Object object) {
        if (object == null) return new byte[0];
        switch (typeName.replace(";", "")) {
            case "File":
                return ((File) object).getAbsolutePath().getBytes();
            case "String":
                return ((String) object).getBytes();
            case "boolean":
                return new byte[]{(boolean) object ? (byte) 1 : 0};
            case "int":
                return new byte[]{new Integer((Integer) object).byteValue()};
            case "double":
                return new byte[]{new Double((Double) object).byteValue()};
            default:
                System.err.println("Not sure what to do with field: " + field.getName() + " of class: " + typeName);
                break;
        }

        return new byte[0];
    }

    public static BinaryIdentifier fromField(Field field) {
        return Arrays.stream(values()).filter(binaryIdentifier -> binaryIdentifier.field.equals(field)).findFirst().orElse(null);
    }

    public static BinaryIdentifier fromByte(byte binary) {
        return Arrays.stream(values()).filter(binaryIdentifier -> binaryIdentifier.binary == binary).findFirst().orElse(null);
    }

    public boolean isArray() {
        return array;
    }
}
