package com.uddernetworks.mspaint.gui.elements;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXScrollPane;
import com.uddernetworks.mspaint.gui.kvselection.EmptySelection;
import com.uddernetworks.mspaint.gui.kvselection.KVCell;
import com.uddernetworks.mspaint.gui.kvselection.KVData;
import com.uddernetworks.mspaint.gui.kvselection.OldNewChange;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.ThemeManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Cell;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SettingKV extends VBox {

    private StringProperty title = new SimpleStringProperty("");
    private StringProperty addText = new SimpleStringProperty("");

    private StringProperty keyPlaceholder = new SimpleStringProperty("");
    private StringProperty valuePlaceholder = new SimpleStringProperty("");

    private SettingsGroup group = new SettingsGroup();
    private VBox vbox = new VBox();
    private JFXScrollPane scrollPane = new JFXScrollPane();
    private JFXListView<KVData> itemSelect = new JFXListView<>();

    private AnchorPane linkHolder = new AnchorPane();
    private Hyperlink addFontText = new Hyperlink();
    private OldNewChange valueChange;
    private OldNewChange keyChange;
    private Function<List<KVData>, Optional<KVData>> kvDataFunction;
    private Consumer<KVCell> kvRemoveConsumer;
    private Consumer<KVData> kvActiveConsumer;
    private Supplier<List<KVData>> initialConsumer;

    public SettingKV() {
        getStyleClass().add("gridpane-theme");

        vbox.setPrefWidth(100);
        vbox.setPadding(new Insets(15, 10, 5, 10));

        group.setTitle(getTitle());
        group.getStyleClass().add("gridpane-theme");
        group.setPrefWidth(710);

        scrollPane.setMaxHeight(200);
        scrollPane.setMinHeight(200);
        scrollPane.setPrefHeight(200);

        scrollPane.getStyleClass().add("result-pane");
        scrollPane.getStyleClass().add("gridpane-theme");

        itemSelect.getStyleClass().add("gridpane-theme");

        scrollPane.getChildren().add(itemSelect);
        vbox.getChildren().add(scrollPane);
        group.setContent(vbox);

        getChildren().add(group);


        linkHolder.setMaxHeight(15);
        linkHolder.setMaxWidth(710);
        linkHolder.minHeight(15);
        linkHolder.prefHeight(15);
        linkHolder.prefWidth(710);

        addFontText.setContentDisplay(ContentDisplay.CENTER);
        addFontText.setLayoutX(223);
        addFontText.setLayoutY(-40);
        addFontText.maxHeight(20);
        addFontText.maxWidth(710);
        addFontText.minHeight(20);
        addFontText.prefHeight(20);
        addFontText.setText(getAddText());
        addFontText.setTextAlignment(TextAlignment.CENTER);
        addFontText.setTextFill(Paint.valueOf("#006aff"));

        linkHolder.getChildren().add(addFontText);

        getChildren().add(linkHolder);
    }

    public void onKeyChange(OldNewChange keyChange) {
        this.keyChange = keyChange;
    }

    public void onValueChange(OldNewChange valueChange) {
        this.valueChange = valueChange;
    }

    public void generateDefault(Function<List<KVData>, Optional<KVData>> kvDataFunction) {
        this.kvDataFunction = kvDataFunction;
    }

    public void onKVRemove(Consumer<KVCell> kvRemoveConsumer) {
        this.kvRemoveConsumer = kvRemoveConsumer;
    }

    public void onKVActive(Consumer<KVData> kvActiveConsumer) {
        this.kvActiveConsumer = kvActiveConsumer;
    }

    public void generateInital(Supplier<List<KVData>> initialConsumer) {
        this.initialConsumer = initialConsumer;
    }

    private ThemeManager.ThemeChanger themeChanger;

    public void initLogic(MainGUI mainGUI) {
        var list = new ArrayList<KVCell>();

        itemSelect.setCellFactory(t -> {
            if (this.themeChanger == null) {
                this.themeChanger = mainGUI.getThemeManager().onDarkThemeChange(this, Map.of(
                        "#itemSelect", "dark",
                        ".remove-entry", "remove-entry-white"
                ));
            }

            var cell = new KVCell(mainGUI, themeChanger, getKeyPlaceholder(), getValuePlaceholder(), currCell -> {
                currCell.getName().textProperty().addListener(((observable, oldValue, newValue) -> {
                    this.keyChange.onChange(currCell, oldValue, newValue);
                    currCell.getItem().setName(newValue);
                }));

                currCell.getPath().textProperty().addListener(((observable, oldValue, newValue) -> {
                    this.valueChange.onChange(currCell, oldValue, newValue);
                    currCell.getItem().setPath(newValue);
                }));

                currCell.getRemoveEntry().setOnAction(event -> {
                    var currItem = currCell.getItem();
                    if (currItem == null) return;
                    if (currItem.isSelected()) {
                        if (itemSelect.getItems().size() == 1) return;
                        list.stream()
                                .map(Cell::getItem)
                                .limit(1)
                                .findFirst()
                                .ifPresent(newActive -> {
                                    this.kvActiveConsumer.accept(newActive);
                                    itemSelect.getItems()
                                            .stream()
                                            .filter(font -> font.equals(newActive))
                                            .findFirst()
                                            .ifPresent(font -> font.setSelected(true));
                                });
                    }

                    this.kvRemoveConsumer.accept(currCell);
                    itemSelect.getItems().remove(currItem);
                    list.remove(currCell);

                    updateRadio(list);
                });

                currCell.getRadio().selectedProperty().addListener(((observable, oldValue, newValue) -> {
                    if (newValue) this.kvActiveConsumer.accept(currCell.getItem());
                }));

                themeChanger.update(100, TimeUnit.MILLISECONDS);
            });
            list.add(cell);
            return cell;
        });

        this.addFontText.setOnAction(event -> {
            var adding = this.kvDataFunction.apply(itemSelect.getItems());
            if (adding.isEmpty()) return;
            itemSelect.getItems().add(adding.get());
            updateRadio(list);
        });

        itemSelect.setFocusTraversable(false);

        itemSelect.setSelectionModel(new EmptySelection());

        itemSelect.getItems().addAll(initialConsumer.get());
    }

    private void updateRadio(List<KVCell> list) {
        list.stream().filter(cell -> cell.getItem() != null)
                .forEach(cell -> cell.getRadio().setSelected(cell.getItem().isSelected()));
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
        this.group.setTitle(title);
    }

    public String getAddText() {
        return addText.get();
    }

    public StringProperty addTextProperty() {
        return addText;
    }

    public void setAddText(String addText) {
        this.addText.set(addText);
        this.addFontText.setText(addText);
    }

    public String getKeyPlaceholder() {
        return keyPlaceholder.get();
    }

    public StringProperty keyPlaceholderProperty() {
        return keyPlaceholder;
    }

    public void setKeyPlaceholder(String keyPlaceholder) {
        this.keyPlaceholder.set(keyPlaceholder);
    }

    public String getValuePlaceholder() {
        return valuePlaceholder.get();
    }

    public StringProperty valuePlaceholderProperty() {
        return valuePlaceholder;
    }

    public void setValuePlaceholder(String valuePlaceholder) {
        this.valuePlaceholder.set(valuePlaceholder);
    }
}
