package cafe.josh.rsmm.viewfx.util;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

/**
 *
 * @author joshua
 */
public class FXMLDialog extends Dialog {
    public FXMLDialog(String fxmlFile) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
        DialogPane pane = new DialogPane();
        fxmlLoader.setRoot(pane);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch(IOException exception) {
            throw new RuntimeException(exception);
        }
        setDialogPane(pane);
    }
}