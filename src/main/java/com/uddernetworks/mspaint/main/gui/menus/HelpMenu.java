package com.uddernetworks.mspaint.main.gui.menus;

import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.gui.BindItem;
import com.uddernetworks.mspaint.main.gui.MenuBind;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class HelpMenu extends MenuBind {

    public HelpMenu(MainGUI mainGUI) {
        super(mainGUI);
    }

    @BindItem(label = "help")
    public void onClickHelp() throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("https://github.com/RubbaBoy/MSPaintIDE/blob/master/README.md"));
    }

    @BindItem(label = "source")
    public void onClickSource() throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("https://github.com/RubbaBoy/MSPaintIDE"));
    }

    @BindItem(label = "submit-a-bug")
    public void onClickSubmitABug() throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("https://github.com/RubbaBoy/MSPaintIDE/issues/new"));
    }

    @BindItem(label = "donate")
    public void onClickDonate() throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("https://paypal.me/RubbaBoy"));
    }

    @BindItem(label = "discord")
    public void onClickDiscord() throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("https://discord.gg/RXmPkPJ"));
    }
}
