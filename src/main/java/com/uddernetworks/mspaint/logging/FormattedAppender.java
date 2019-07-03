package com.uddernetworks.mspaint.logging;

import javafx.application.Platform;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.model.ReadOnlyStyledDocument;
import org.fxmisc.richtext.model.SegmentOps;

import java.util.HashMap;
import java.util.Map;

import static org.apache.log4j.Level.*;

public class FormattedAppender extends ConsoleAppender {

    private static Map<Level, String> PRE_STYLES = Map.of(
            OFF, "-fx-fill: WHITE;",
            FATAL, "-fx-fill: WHITE;-fx-background-color: #370000;-fx-font-weight: bold;",
//            ERROR, "-fx-fill: RED;",
            ERROR, "-fx-fill: WHITE;-fx-background-color: #370000;",
            WARN, "-fx-fill: ORANGE;",
            INFO, "-fx-fill: WHITE;",
            DEBUG, "-fx-fill: GRAY;",
            TRACE, "-fx-fill: GRAY;",
            ALL, "-fx-fill: WHITE;"
    );

    private static final Map<Level, String> STYLE_MAP = new HashMap<>();

    private static InlineCssTextArea output;

    public static void activate(InlineCssTextArea output) {
        FormattedAppender.output = output;
        output.getStyleClass().add("output-theme");

        PRE_STYLES.forEach((level, style) -> STYLE_MAP.put(level, style + "-fx-font-family: Monospace;"));
    }

    @Override
    public void append(LoggingEvent event) {
        super.append(event);

        if (output == null) return;
        Platform.runLater(() -> {
            var style = STYLE_MAP.get(event.getLevel());
            printOut(getLayout().format(event), style);

            if (this.layout.ignoresThrowable()) {
                String[] stackTrace = event.getThrowableStrRep();
                if (stackTrace != null) {
                    for (var value : stackTrace) {
                        printOut(value + Layout.LINE_SEP, style);
                    }
                }
            }
        });
    }

    private void printOut(String text, String style) {
        output.append(ReadOnlyStyledDocument.fromString(text, style, style, SegmentOps.styledTextOps()));
    }
}
