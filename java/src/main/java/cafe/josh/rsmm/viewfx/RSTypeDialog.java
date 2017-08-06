package cafe.josh.rsmm.viewfx;

import cafe.josh.joshfx.FXMLDialog;
import cafe.josh.rsmm.model.RSType;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Modality;

public class RSTypeDialog extends FXMLDialog<RSType> {
    @FXML
    private RadioButton osrsButton;
    @FXML
    private RadioButton rs3Button;

    private final ToggleGroup group;

    public RSTypeDialog() {
        super("/fxml/rs_type_chooser.fxml");
        initModality(Modality.APPLICATION_MODAL);
        resultConverterProperty().setValue(buttonType -> buttonType == ButtonType.OK ? getSelectedType() : null);
        group = new ToggleGroup();
        osrsButton.setToggleGroup(group);
        rs3Button.setToggleGroup(group);

        osrsButton.setUserData(RSType.OSRS);
        rs3Button.setUserData(RSType.RS3);
    }

    public RSType getSelectedType() {
        Toggle b = group.getSelectedToggle();
        if(b == null) {
            return null;
        }

        return (RSType) b.getUserData();
    }
}
