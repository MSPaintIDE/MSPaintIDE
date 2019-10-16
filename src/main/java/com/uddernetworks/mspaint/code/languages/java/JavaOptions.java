package com.uddernetworks.mspaint.code.languages.java;

import com.uddernetworks.mspaint.code.LangGUIOptionRequirement;
import com.uddernetworks.mspaint.code.languages.LanguageSettings;
import com.uddernetworks.mspaint.code.languages.Option;

/**
 * All the Java language options. All options that are linked as {@link LangGUIOptionRequirement#OPTIONAL} must require
 * the {@link LanguageSettings#getSettingOptional} and other Optional-returning methods, as they may not be set.
 */
public interface JavaOptions extends Option {
}
