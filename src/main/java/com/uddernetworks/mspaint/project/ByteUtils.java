package com.uddernetworks.mspaint.project;

import java.util.ArrayList;
import java.util.List;

public class ByteUtils {
    public static  byte[] byteListToArray(List<Byte> byteList) {
        byte[] byteArray = new byte[byteList.size()];
        for (int i = 0; i < byteList.size(); i++) byteArray[i] = byteList.get(i);
        return byteArray;
    }

    public static List<Byte> byteArrayToList(byte[] byteArray) {
        List<Byte> byteList = new ArrayList<>();
        for (byte b : byteArray) byteList.add(b);
        return byteList;
    }
}
