package net.jmhertlein.rsmm.controller.item;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by joshua on 1/18/16.
 */
public class NavigateToGELimitsAction extends AbstractAction {
    public NavigateToGELimitsAction() {
        super("GE Limits");
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            Desktop.getDesktop().browse(new URI("http://runescape.wikia.com/wiki/Ge_limits"));
        } catch (URISyntaxException | IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error Navigating To RSWiki.", JOptionPane.ERROR_MESSAGE);
        }
    }
}
