package net.jmhertlein.rsmm.view;

import javax.swing.*;
import java.awt.*;

/**
 * Created by joshua on 1/16/16.
 */
public class FatPanel extends JPanel {
    public FatPanel(Component p) {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.weightx = 1;
        add(p, c);
    }
}
