package com.uddernetworks.mspaint.main;

import com.uddernetworks.mspaint.main.gui.BindItem;
import com.uddernetworks.mspaint.main.gui.MenuBind;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Menu;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CustomMenu extends Menu {

    private StringProperty bindClass;

    public CustomMenu() {

    }

    public void initialize(MainGUI mainGUI) {
        try {
            Class<? extends MenuBind> menuClass = (Class<? extends MenuBind>) Class.forName("com.uddernetworks.mspaint.main.gui.menus." + getBindClass());
            if (menuClass == null) return;

            MenuBind menuBind = menuClass.getConstructor(MainGUI.class).newInstance(mainGUI);

            Map<String, CustomMenuItem> events = new HashMap<>();

            getItems()
                    .stream()
                    .filter(CustomMenuItem.class::isInstance)
                    .map(CustomMenuItem.class::cast)
                    .forEach(menuItem -> {
                        events.put(menuItem.getClickLabel(), menuItem);
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

}
