package com.uddernetworks.mspaint.project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public enum BinaryIdentifier {
//    INPUT_LOCATION(5, "inputLocation"),
//    HIGHLIGHT_LOCATION(6, "highlightLocation"),
//    OBJECT_LOCATION(7, "objectLocation"),
//    CLASS_LOCATION(8, "classLocation"),
//    JAR_FILE(9, "jarFile"),
//    LIBRARY_LOCATION(10, "libraryLocation"),
//    OTHER_LOCATION(11, "otherLocation"),
//    COMPILER_OUTPUT(12, "compilerOutput"),
//    APP_OUTPUT(13, "appOutput"),
//    NAME(14, "name"),
//    LANGUAGE(15, "language"),
//    SYNTAX_HIGHLIGHT(16, "syntaxHighlight"),
//    COMPILE(17, "compile"),
//    EXECUTE(18, "execute"),
//    ACTIVE_FONT_NAME(19, "activeFont"),
//    FONT_NAMES(20, "fontNames"),
//    FONT_PATHS(24, "fontPaths");
    MAP(1, "map");

    private static Logger LOGGER = LoggerFactory.getLogger(BinaryIdentifier.class);

    public static final byte START_VALUE = 2;
    public static final byte END_VALUE = 3;
    public static final byte START_ARRAY = 21;
    public static final byte BREAK_ARRAY = 22;
    public static final byte END_ARRAY = 23;
    public static final byte START_MAP = 24;
    public static final byte BREAK_KV = 25;
    public static final byte BREAK_MAP = 26;
    public static final byte END_MAP = 27;

    private byte binary;
    private Field field;
    private String typeName;
    private Special special = Special.NORMAL;
    private List<Type> types = new ArrayList<>();

    BinaryIdentifier(int binary, String field) {
        this.binary = (byte) binary;

        try {
            this.field = PPFProject.class.getDeclaredField(field);
            this.field.setAccessible(true);
            this.typeName = this.field.getType().getName().replaceAll("^.*\\.", "").replace(";", "");

            var pt = (ParameterizedType) this.field.getGenericType();
            types.addAll(Arrays.asList(pt.getActualTypeArguments()));

            if (this.field.getType().isArray()) {
                this.special = Special.ARRAY;
            } else if (this.typeName.equals("Map")) {
                this.special = Special.MAP;
            }
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
            switch (special) {
                case ARRAY:
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

                        if (!buffer.isEmpty()) {
                            array.add(processSetBytes(ByteUtils.byteListToArray(buffer)));
                        }
                    }

                    var baseClass = Class.forName(this.field.getType().getCanonicalName().replace("[", "").replace("]", ""));

                    var arr = Array.newInstance(baseClass, array.size());
                    for (int i = 0; i < array.size(); i++) {
                        Array.set(arr, i, array.get(i));
                    }

                    field.set(ppfProject, arr);
                    break;
                case MAP:
                    var keyClass = Class.forName(types.get(0).getTypeName());
                    var valueClass = Class.forName(types.get(1).getTypeName());

                    var map = new HashMap<>();
                    if (bytes[0] == START_MAP) {
                        var buffer = new ArrayList<Byte>();
                        var readingKey = true;
                        Object key = null;

                        for (int i = 1; i < bytes.length; i++) {
                            var curr = bytes[i];
                            if (curr == BREAK_KV) {
                                key = processSetBytes(ByteUtils.byteListToArray(buffer), keyClass.getSimpleName());
                                readingKey = false;

                                buffer.clear();
                            } else if (curr == BREAK_MAP) {
                                map.put(key, processSetBytes(ByteUtils.byteListToArray(buffer), valueClass.getSimpleName()));
                                key = null;
                                readingKey = true;

                                buffer.clear();
                            } else if (curr != END_MAP){
                                buffer.add(curr);
                            }
                        }

                        if (!buffer.isEmpty() && !readingKey) {
                            map.put(key, processSetBytes(ByteUtils.byteListToArray(buffer), valueClass.getSimpleName()));
                        }
                    }

                    field.set(ppfProject, map);
                    break;
                case NORMAL:
                    field.set(ppfProject, processSetBytes(bytes));
                    break;
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    private Object processSetBytes(byte[] bytes) {
        return processSetBytes(bytes, this.typeName);
    }

    private Object processSetBytes(byte[] bytes, String type) {
        String string = new String(bytes);
        switch (type) {
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
                System.err.println("[Process] Not sure what to do with data value: " + string + " of class: " + typeName);
                break;
        }

        return null;
    }

    public byte[] getValue(PPFProject ppfProject) {
        try {
            if (field.get(ppfProject) == null) return new byte[0];
            var bytes = new ArrayList<Byte>();
            switch (special) {
                case ARRAY:
                    Object[] list = (Object[]) field.get(ppfProject);
                    bytes.add(START_ARRAY);
                    Arrays.stream(list).forEach(element -> {
                        bytes.addAll(ByteUtils.byteArrayToList(processGetBytes(element)));
                        bytes.add(BREAK_ARRAY);
                    });
                    bytes.remove(bytes.size() - 1);
                    bytes.add(END_ARRAY);
                    return ByteUtils.byteListToArray(bytes);
                case MAP:
                    var map = (Map<Object, Object>) field.get(ppfProject);
                    var keyClass = Class.forName(types.get(0).getTypeName());
                    var valueClass = Class.forName(types.get(1).getTypeName());

                    bytes.add(START_MAP);
                    map.forEach((key, value) -> {
                        bytes.addAll(ByteUtils.byteArrayToList(processGetBytes(key, keyClass.getSimpleName())));
                        bytes.add(BREAK_KV);
                        bytes.addAll(ByteUtils.byteArrayToList(processGetBytes(value, valueClass.getSimpleName())));
                        bytes.add(BREAK_MAP);
                    });
                    bytes.remove(bytes.size() - 1);
                    bytes.add(END_MAP);
                    return ByteUtils.byteListToArray(bytes);
                case NORMAL:
                    return processGetBytes(field.get(ppfProject));
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    private byte[] processGetBytes(Object object) {
        return processGetBytes(object, this.typeName);
    }

    private byte[] processGetBytes(Object object, String type) {
        if (object == null) return new byte[0];
        switch (type) {
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
                System.err.println("[Get] Not sure what to do with field: " + field.getName() + " of class: " + typeName);
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

    enum Special {
        NORMAL, ARRAY, MAP
    }
}
