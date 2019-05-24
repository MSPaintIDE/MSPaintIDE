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
     * button. Note that there is a 6 option limit on these, though more may be added to the API if really necessary.
     * TODO: Double-check 6 option limit
     */
    BOTTOM_DISPLAY
}
