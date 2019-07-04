package com.uddernetworks.mspaint.discord;

import com.uddernetworks.mspaint.code.languages.Language;

public interface RPCManager {

    /**
     * Initializes and starts displaying the RPC.
     */
    void init();

    /**
     * Updates the RPC to the current project. This will almost certainly only be ran once, but may not be immediate.
     */
    void updateProject();

    /**
     * Sets the file that is currently being edited, or at least an estimation of what is being edited.
     *
     * @param fileName The name of the file being edited
     */
    void setFileEditing(String fileName);

    /**
     * Sets the language to be displayed. This will again, almost certainly be ran once.
     *
     * @param language The language to display
     */
    void setLanguage(Language language);
}
