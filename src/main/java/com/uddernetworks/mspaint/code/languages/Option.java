package com.uddernetworks.mspaint.code.languages;

import com.uddernetworks.mspaint.code.LangGUIOptionRequirement;

public interface Option {
    String getName();
    Option fromName(String name);
    Class getType();
    LangGUIOptionRequirement getRequirement();
    int ordinal();
}
