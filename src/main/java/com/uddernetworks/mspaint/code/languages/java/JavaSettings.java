package com.uddernetworks.mspaint.code.languages.java;

import com.uddernetworks.mspaint.code.languages.LanguageSettings;

public class JavaSettings extends LanguageSettings<JavaOptions> {

    protected JavaSettings() {
        super("Java");
    }

    @Override
    protected JavaOptions stringToAccessor(String name) {
        return JavaOptions.fromName(name);
    }

    @Override
    protected String accessorToString(JavaOptions accessor) {
        return accessor.getName();
    }
}
