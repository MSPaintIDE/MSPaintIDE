package com.uddernetworks.mspaint.gui.fonts;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.collections.ObservableList;
import javafx.scene.control.MultipleSelectionModel;

import java.util.ArrayList;

public class EmptySelection extends MultipleSelectionModel<OCRFont> {

    @Override
    public ObservableList<Integer> getSelectedIndices() {
        return new ObservableListWrapper<>(new ArrayList<>());
    }

    @Override
    public ObservableList<OCRFont> getSelectedItems() {
        return null;
    }

    @Override
    public void selectIndices(int index, int... indices) {

    }

    @Override
    public void selectAll() {

    }

    @Override
    public void selectFirst() {

    }

    @Override
    public void selectLast() {

    }

    @Override
    public void clearAndSelect(int index) {

    }

    @Override
    public void select(int index) {

    }

    @Override
    public void select(OCRFont obj) {

    }

    @Override
    public void clearSelection(int index) {

    }

    @Override
    public void clearSelection() {

    }

    @Override
    public boolean isSelected(int index) {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void selectPrevious() {

    }

    @Override
    public void selectNext() {

    }
}
