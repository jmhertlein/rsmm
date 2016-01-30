package net.jmhertlein.rsmm.view.profit;

import net.jmhertlein.rsmm.model.RSInteger;

import javax.swing.*;
import java.awt.*;

/**
 * Created by joshua on 1/29/16.
 */
public class GPLabel extends JLabel {
    private RSInteger px;

    public GPLabel() {
        this.px = new RSInteger(-1);
    }

    public GPLabel(RSInteger px) {
        this.px = px;
    }

    public RSInteger getPx() {
        return px;
    }

    public void setPx(RSInteger px) {
        this.px = px;
        setText(px.toString());

        if (px.intValue() > 0) {
            setForeground(Color.GREEN.darker().darker());
        } else if (px.intValue() == 0) {
            setForeground(Color.BLACK);
        } else {
            setForeground(Color.RED);
        }
    }
}
