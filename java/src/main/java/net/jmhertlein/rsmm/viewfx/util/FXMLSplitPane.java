package net.jmhertlein.rsmm.viewfx.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;

import java.io.IOException;

/**
 * Created by joshua on 3/11/16.
 */
public class FXMLSplitPane extends SplitPane {
    public FXMLSplitPane(String fxmlFile) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch(IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
