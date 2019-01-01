package com.uddernetworks.mspaint.main.gui;

import com.uddernetworks.mspaint.main.MainGUI;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MaterialMenu extends Menu {

    private Label label;
    private MaterialMenu parentMenu;
    private StringProperty bindClass;
    private StringProperty clickLabel;

    public MaterialMenu() {
        parentMenuProperty().addListener((observable, oldValue, newValue) -> {
            parentMenu = (MaterialMenu) newValue;

            label = new Label();

            label.setPrefWidth(180);
            label.setPadding(new Insets(0, 0, 0, 5));

            label.setContentDisplay(ContentDisplay.RIGHT);
            label.setGraphicTextGap(0);
            setGraphic(label);
        });
    }

    public void initialize(MainGUI mainGUI) {
        if (parentMenu != null) {
            label.setText(getText());
            setText(null);
        }

        try {
            Class<? extends MenuBind> menuClass = (Class<? extends MenuBind>) Class.forName("com.uddernetworks.mspaint.main.gui.menus." + getBindClass());
            if (menuClass == null) return;

            String prepend = parentMenu != null ? getClickLabel() + "." : "";
            MenuBind menuBind = menuClass.getConstructor(MainGUI.class).newInstance(mainGUI);

            Map<String, MaterialMenuItem> events = new HashMap<>();

            getItems()
                    .stream()
                    .filter(MaterialMenuItem.class::isInstance)
                    .map(MaterialMenuItem.class::cast)
                    .forEach(menuItem -> {
                        events.put(prepend + menuItem.getClickLabel(), menuItem);
                        menuItem.initialize();
                    });

            Arrays.stream(menuClass.getDeclaredMethods())
                    .filter(method -> method.isAnnotationPresent(BindItem.class))
                    .forEach(method -> {
                        BindItem bindItem = method.getAnnotation(BindItem.class);
                        if (!events.containsKey(bindItem.label())) return;

                        events.get(bindItem.label()).setOnAction(event -> {
                            try {
                                method.setAccessible(true);
                                method.invoke(menuBind);
                            } catch (ReflectiveOperationException e) {
                                e.printStackTrace();
                            }
                        });
                    });
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    public final void setBindClass(String bindClass) {
        bindClassProperty().set(bindClass);
    }

    public final String getBindClass() {
        return bindClass == null ? null : bindClass.get();
    }

    public final StringProperty bindClassProperty() {
        if (bindClass == null) {
            bindClass = new SimpleStringProperty(this, null);
        }

        return bindClass;
    }

    public final void setClickLabel(String clickLabel) {
        clickLabelProperty().set(clickLabel);
    }

    public final String getClickLabel() {
        return clickLabel == null ? null : clickLabel.get();
    }

    public final StringProperty clickLabelProperty() {
        if (clickLabel == null) {
            clickLabel = new SimpleStringProperty(this, null);
        }

        return clickLabel;
    }
}
