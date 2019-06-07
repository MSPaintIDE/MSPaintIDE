package com.uddernetworks.mspaint.gui.menus;

import com.uddernetworks.mspaint.code.BuildSettings;
import com.uddernetworks.mspaint.code.ImageClass;
import com.uddernetworks.mspaint.gui.BindItem;
import com.uddernetworks.mspaint.gui.MenuBind;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.paintassist.PaintAssist;
import com.uddernetworks.paintassist.actions.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class AssistantMenu extends MenuBind {

    private static Logger LOGGER = LoggerFactory.getLogger(AssistantMenu.class);

    private PaintAssist paintAssist;

    public AssistantMenu(MainGUI mainGUI) {
        super(mainGUI);

        this.paintAssist = mainGUI.getStartupLogic().getPaintAssist();
    }

    @BindItem(label = "login")
    public void onClickLogin() {
        var authenticator = this.paintAssist.getAuthenticator();
        if (!authenticator.isAuthenticated()) {
            LOGGER.info("Activating PaintAssist...");

            this.paintAssist.activate().ifPresent(tokenInfo -> {
                try {
                    var actionListener = this.paintAssist.getActionListener();
                    actionListener.clearListeners();
                    actionListener.listen((actions, time) -> {
                        try {
                            if (actions.contains(Action.STOP)) {
                                this.mainGUI.getStartupLogic().getRunningCodeManager().stopRunning();
                                return;
                            }

                            if (!actions.contains(Action.COMPILE) && !actions.contains(Action.RUN) && actions.contains(Action.HIGHLIGHT)) {
                                LOGGER.info("Just highlighting");
                                var language = mainGUI.getCurrentLanguage();
                                language.indexFiles().ifPresentOrElse(imageClasses -> {
                                    try {
                                        imageClasses.forEach(ImageClass::scan);
                                        language.highlightAll(imageClasses);
                                    } catch (IOException e) {
                                        LOGGER.error("Error while highlighting images", e);
                                    }
                                }, () -> LOGGER.error("Error while finding ImageClasses, aborting..."));
                            } else {
                                var action = BuildSettings.DEFAULT;
                                if (actions.contains(Action.RUN)) {
                                    action = BuildSettings.EXECUTE;
                                } else {
                                    action = BuildSettings.DONT_EXECUTE;
                                }

                                LOGGER.info("Building with action: " + action);
                                this.mainGUI.fullCompile(action);
                            }
                        } catch (Exception e) {
                            LOGGER.error("An error occurred while processing PaintAssistant command", e);
                        }
                    });

                    var userProfile = this.paintAssist.getAuthenticator().getOAuth2().userinfo();
                    var userInfoPlus = userProfile.get().execute();

                    mainGUI.setProfileNameText(userInfoPlus.getName());
                    mainGUI.setProfilePicture(userInfoPlus.getPicture());
                } catch (IOException e) {
                    LOGGER.error("There was an error getting user profile data", e);
                }
            });

            LOGGER.info("Finished loading PaintAssist");
        } else {
            LOGGER.info("You're already authenticated!");
        }
    }

    @BindItem(label = "logout")
    public void onClickLogout() {
        var authenticator = this.paintAssist.getAuthenticator();
        if (authenticator.isAuthenticated()) {
            LOGGER.info("Logging out and deleting local token data...");

            authenticator.unAuthenticate();
            mainGUI.setProfileNameText(null);
            mainGUI.setProfilePicture(null);

            LOGGER.info("Logged out");
        } else {
            LOGGER.info("You're already logged out!");
        }
    }

    @BindItem(label = "help")
    public void onClickHelp() {
        // TODO: Help
    }
}
