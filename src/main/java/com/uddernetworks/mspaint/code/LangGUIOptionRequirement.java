package com.uddernetworks.mspaint.code;

public enum LangGUIOptionRequirement {

    /**
     * The option is optional, and does not need to be filled in for minimal functionality.
     */
    OPTIONAL,

    /**
     * The option is required to compile/execute/whatever code.
     */
    REQUIRED,

    /**
     * Available for boolean values ONLY, showing a checkbox of the value at the bottom of the GUI above the start
     * button.
     */
    BOTTOM_DISPLAY,

    /**
     * The option is displayed but can not be changed by the user; only programmatically. Used for things like go's
     * src folder.
     */
    UNMODIFIABLE
}
