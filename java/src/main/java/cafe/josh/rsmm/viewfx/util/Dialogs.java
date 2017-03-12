package cafe.josh.rsmm.viewfx.util;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;

import java.util.stream.Stream;

/**
 *
 * @author joshua
 */
public class Dialogs {
    public static void showMessage(String title, String header, String message, String expandable) {
        Dialog d = new Dialog();
        d.setTitle(title);
        d.setHeaderText(header);
        d.setContentText(message);
        TextArea expanded = new TextArea(expandable);
        expanded.setEditable(false);
        d.getDialogPane().setExpandableContent(new ScrollPane(expanded));
        d.getDialogPane().getButtonTypes().add(ButtonType.OK);
        d.showAndWait();
    }

    public static void showMessage(String title, String header, String message) {
        Dialog d = new Dialog();
        d.setTitle(title);
        d.setHeaderText(header);
        d.setContentText(message);
        d.getDialogPane().getButtonTypes().add(ButtonType.OK);
        d.showAndWait();
    }

    public static void showMessage(String title, String header, Exception ex) {
        String consolidatedTrace = Stream.of(ex.getStackTrace())
                .map(StackTraceElement::toString)
                .reduce((l, r) -> l + "\n" + r)
                .orElse("No trace available.");
        showMessage(title, header, ex.getMessage(), consolidatedTrace);
    }
}