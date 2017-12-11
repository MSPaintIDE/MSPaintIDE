package com.uddernetworks.mspaint.main;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Objects;

public class MainWindow {

    private JPanel panel1;
    private JButton changeInputImage;
    private JTextPane textAreaOutput;
    private JButton changeHighlightImage;
    private JButton changeCacheFile;
    private JButton changeClassOutput;
    private JButton changeLetterDir;
    private JButton compilerOutput;
    private JButton programOutput;


    private JTextField inputName;
    private JTextField highlightedImage;
    private JTextField cacheFile;
    private JTextField classOutput;
    private JTextField letterDirectory;
    private JTextField compilerOutputValue;
    private JTextField programOutputValue;
    private JButton highlightButton;
    private JButton compileExecuteButton;

    private FileFilter imageFilter = new FileNameExtensionFilter("Image files", "png");
    private FileFilter txtFilter = new FileNameExtensionFilter("Text document", "txt");
    private FolderFilter folderFilter = new FolderFilter();

    public void registerThings(Main main, File currentFile) {
        inputName.setText(main.getInputImage());
        highlightedImage.setText(main.getHighlightedFile());
        cacheFile.setText(main.getObjectFile());
        classOutput.setText(main.getClassOutput());
        letterDirectory.setText(main.getLetterDirectory());
        compilerOutputValue.setText(main.getCompilerOutput());
        programOutputValue.setText(main.getAppOutput());


        changeInputImage.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(imageFilter);
            fc.setSelectedFile(currentFile);
            int returnVal = fc.showOpenDialog(panel1);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File selected = fc.getSelectedFile();
                inputName.setText(selected.getAbsolutePath());
                main.setInputImage(fc.getSelectedFile());
            }
        });

        changeHighlightImage.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(currentFile);
            fc.setFileFilter(imageFilter);
            int returnVal = fc.showOpenDialog(panel1);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File selected = fc.getSelectedFile();
                highlightedImage.setText(selected.getAbsolutePath());
                main.setHighlightedFile(fc.getSelectedFile());
            }
        });

        changeCacheFile.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(currentFile);
            fc.setFileFilter(txtFilter);
            int returnVal = fc.showOpenDialog(panel1);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File selected = fc.getSelectedFile();
                cacheFile.setText(selected.getAbsolutePath());
                main.setObjectFile(fc.getSelectedFile());
            }
        });

        changeClassOutput.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(currentFile);
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = fc.showOpenDialog(panel1);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File selected = fc.getSelectedFile();
                classOutput.setText(selected.getAbsolutePath());
                main.setClassOutput(fc.getSelectedFile());
            }
        });

        changeLetterDir.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(currentFile);
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = fc.showOpenDialog(panel1);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File selected = fc.getSelectedFile();
                letterDirectory.setText(selected.getAbsolutePath());
                main.setLetterDirectory(fc.getSelectedFile());
            }
        });

        compilerOutput.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(currentFile);
            fc.setFileFilter(imageFilter);
            int returnVal = fc.showOpenDialog(panel1);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File selected = fc.getSelectedFile();
                compilerOutputValue.setText(selected.getAbsolutePath());
                main.setCompilerOutput(fc.getSelectedFile());
            }
        });

        programOutput.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(currentFile);
            fc.setFileFilter(imageFilter);
            int returnVal = fc.showOpenDialog(panel1);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File selected = fc.getSelectedFile();
                programOutputValue.setText(selected.getAbsolutePath());
                main.setAppOutput(fc.getSelectedFile());
            }
        });


        addChangeListener(inputName, e -> main.setInputImage(new File(inputName.getText())));

        addChangeListener(highlightedImage, e -> main.setHighlightedFile(new File(highlightedImage.getText())));

        addChangeListener(cacheFile, e -> main.setObjectFile(new File(cacheFile.getText())));

        addChangeListener(classOutput, e -> main.setClassOutput(new File(classOutput.getText())));

        addChangeListener(letterDirectory, e -> main.setLetterDirectory(new File(letterDirectory.getText())));

        addChangeListener(compilerOutputValue, e -> main.setCompilerOutput(new File(compilerOutputValue.getText())));

        addChangeListener(programOutputValue, e -> main.setAppOutput(new File(programOutputValue.getText())));


        highlightButton.addActionListener(e -> new Thread(() -> {
            try {
                main.highlight(true);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }).start());

        compileExecuteButton.addActionListener(e -> new Thread(() -> {
            try {
                main.compile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }).start());
    }

    public JTextPane getTextAreaOutput() {
        return textAreaOutput;
    }


    private static void addChangeListener(JTextComponent text, ChangeListener changeListener) {
        Objects.requireNonNull(text);
        Objects.requireNonNull(changeListener);
        DocumentListener dl = new DocumentListener() {
            private int lastChange = 0, lastNotifiedChange = 0;

            @Override
            public void insertUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                lastChange++;
                SwingUtilities.invokeLater(() -> {
                    if (lastNotifiedChange != lastChange) {
                        lastNotifiedChange = lastChange;
                        changeListener.stateChanged(new ChangeEvent(text));
                    }
                });
            }
        };
        text.addPropertyChangeListener("document", (PropertyChangeEvent e) -> {
            Document d1 = (Document) e.getOldValue();
            Document d2 = (Document) e.getNewValue();
            if (d1 != null) d1.removeDocumentListener(dl);
            if (d2 != null) d2.addDocumentListener(dl);
            dl.changedUpdate(null);
        });
        Document d = text.getDocument();
        if (d != null) d.addDocumentListener(dl);
    }


    public void display() {
        JFrame frame = new JFrame();
        frame.setContentPane(panel1);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setTitle("MS Paint IDE");
        frame.setResizable(false);
        frame.setVisible(true);
        frame.pack();
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(10, 10, 0, 15), -1, -1));
        panel1.setAutoscrolls(false);
        panel1.setMaximumSize(new Dimension(527, 712));
        panel1.setMinimumSize(new Dimension(527, 706));
        panel1.setPreferredSize(new Dimension(527, 706));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(3, 4, new Insets(10, 0, 10, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(508, 692), new Dimension(508, 692), new Dimension(508, 692), 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(7, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, 1, 1, new Dimension(150, -1), new Dimension(150, -1), new Dimension(150, -1), 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Input Image:");
        panel3.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Highlighted Image:");
        panel3.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Cache File:");
        panel3.add(label3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Class Output:");
        panel3.add(label4, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Letter Directory:");
        panel3.add(label5, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Compiler Output:");
        panel3.add(label6, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Program Output:");
        panel3.add(label7, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(7, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel4, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, 1, 1, new Dimension(100, -1), new Dimension(100, -1), new Dimension(100, -1), 0, false));
        changeInputImage = new JButton();
        changeInputImage.setHorizontalAlignment(0);
        changeInputImage.setText("Change");
        panel4.add(changeInputImage, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), new Dimension(100, -1), new Dimension(100, -1), 0, false));
        changeHighlightImage = new JButton();
        changeHighlightImage.setText("Change");
        panel4.add(changeHighlightImage, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), new Dimension(100, -1), new Dimension(100, -1), 0, false));
        changeCacheFile = new JButton();
        changeCacheFile.setText("Change");
        panel4.add(changeCacheFile, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), new Dimension(100, -1), new Dimension(100, -1), 0, false));
        changeClassOutput = new JButton();
        changeClassOutput.setText("Change");
        panel4.add(changeClassOutput, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), new Dimension(100, -1), new Dimension(100, -1), 0, false));
        changeLetterDir = new JButton();
        changeLetterDir.setText("Change");
        panel4.add(changeLetterDir, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), new Dimension(100, -1), new Dimension(100, -1), 0, false));
        compilerOutput = new JButton();
        compilerOutput.setText("Change");
        panel4.add(compilerOutput, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), new Dimension(100, -1), new Dimension(100, -1), 0, false));
        programOutput = new JButton();
        programOutput.setText("Change");
        panel4.add(programOutput, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), new Dimension(100, -1), new Dimension(100, -1), 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(7, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel5, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        inputName = new JTextField();
        inputName.setEnabled(true);
        inputName.setFocusable(true);
        inputName.setHorizontalAlignment(2);
        inputName.setRequestFocusEnabled(true);
        inputName.setText("");
        panel5.add(inputName, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(1000, -1), null, 0, false));
        highlightedImage = new JTextField();
        highlightedImage.setEnabled(true);
        highlightedImage.setFocusable(true);
        highlightedImage.setHorizontalAlignment(2);
        highlightedImage.setRequestFocusEnabled(true);
        highlightedImage.setText("");
        panel5.add(highlightedImage, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(1000, -1), null, 0, false));
        cacheFile = new JTextField();
        cacheFile.setEnabled(true);
        cacheFile.setFocusable(true);
        cacheFile.setHorizontalAlignment(2);
        cacheFile.setRequestFocusEnabled(true);
        cacheFile.setText("");
        panel5.add(cacheFile, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(1000, -1), null, 0, false));
        classOutput = new JTextField();
        classOutput.setEnabled(true);
        classOutput.setFocusable(true);
        classOutput.setHorizontalAlignment(2);
        classOutput.setRequestFocusEnabled(true);
        classOutput.setText("");
        panel5.add(classOutput, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(1000, -1), null, 0, false));
        letterDirectory = new JTextField();
        letterDirectory.setEnabled(true);
        letterDirectory.setFocusable(true);
        letterDirectory.setHorizontalAlignment(2);
        letterDirectory.setRequestFocusEnabled(true);
        letterDirectory.setText("");
        panel5.add(letterDirectory, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(1000, -1), null, 0, false));
        compilerOutputValue = new JTextField();
        compilerOutputValue.setEnabled(true);
        compilerOutputValue.setFocusable(true);
        compilerOutputValue.setHorizontalAlignment(2);
        compilerOutputValue.setRequestFocusEnabled(true);
        compilerOutputValue.setText("");
        panel5.add(compilerOutputValue, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(1000, -1), null, 0, false));
        programOutputValue = new JTextField();
        programOutputValue.setEnabled(true);
        programOutputValue.setFocusable(true);
        programOutputValue.setHorizontalAlignment(2);
        programOutputValue.setRequestFocusEnabled(true);
        programOutputValue.setText("");
        panel5.add(programOutputValue, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(1000, -1), null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel2.add(scrollPane1, new GridConstraints(2, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(500, 380), new Dimension(500, 380), new Dimension(500, 380), 0, false));
        textAreaOutput = new JTextPane();
        textAreaOutput.setEditable(false);
        textAreaOutput.setMaximumSize(new Dimension(-1, -1));
        textAreaOutput.setMinimumSize(new Dimension(-1, -1));
        textAreaOutput.setPreferredSize(new Dimension(-1, -1));
        scrollPane1.setViewportView(textAreaOutput);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel6, new GridConstraints(1, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        highlightButton = new JButton();
        highlightButton.setAlignmentX(0.5f);
        highlightButton.setText("Syntax Highlight");
        panel6.add(highlightButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        compileExecuteButton = new JButton();
        compileExecuteButton.setAlignmentX(0.5f);
        compileExecuteButton.setText("Compile/Execute");
        panel6.add(compileExecuteButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

    private static class FolderFilter extends FileFilter {
        @Override
        public boolean accept(File file) {
            return file.isDirectory();
        }

        @Override
        public String getDescription() {
            return "Directories";
        }
    }
}
